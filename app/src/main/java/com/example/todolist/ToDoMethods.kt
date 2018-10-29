package com.example.todolist

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.todolist.db.DbSettings
import com.example.todolist.db.ToDosDatabaseHelper
import com.example.todolist.model.ToDo
import java.util.*

class ToDoMethods(application: Application): AndroidViewModel(application){
    private var _todoDBHelper: ToDosDatabaseHelper = ToDosDatabaseHelper(application)
    private var _todoList: MutableLiveData<ArrayList<ToDo>> = MutableLiveData()

    fun getToDos(prefs: SharedPreferences): MutableLiveData<ArrayList<ToDo>> {
        loadToDos(prefs)
        return _todoList
    }

    private fun loadToDos(prefs: SharedPreferences) {
        val newFavourites: ArrayList<ToDo> = ArrayList()
        val database: SQLiteDatabase = this._todoDBHelper.readableDatabase

        // Check Shared Preferences
        val cursor: Cursor

        if (prefs.getBoolean(SharedPrefKeys.sortByTitle, false)) {
            cursor = database.query(
                    DbSettings.DBEntry.TABLE,
                    arrayOf(
                            DbSettings.DBEntry.ID,
                            DbSettings.DBEntry.COL_TITLE,
                            DbSettings.DBEntry.COL_CATEGORY,
                            DbSettings.DBEntry.COL_DETAIL,
                            DbSettings.DBEntry.COL_IMPORTANCE,
                            DbSettings.DBEntry.COL_DONE,
                            DbSettings.DBEntry.COL_DATE_COMPLETED,
                            DbSettings.DBEntry.COL_DUE
                    ),
                    null, null, null, null, DbSettings.DBEntry.COL_TITLE
            )
        }else if(prefs.getBoolean(SharedPrefKeys.sortByImportance, false)){
            cursor = database.query(
                    DbSettings.DBEntry.TABLE,
                    arrayOf(
                            DbSettings.DBEntry.ID,
                            DbSettings.DBEntry.COL_TITLE,
                            DbSettings.DBEntry.COL_CATEGORY,
                            DbSettings.DBEntry.COL_DETAIL,
                            DbSettings.DBEntry.COL_DUE,
                            DbSettings.DBEntry.COL_IMPORTANCE,
                            DbSettings.DBEntry.COL_DONE,
                            DbSettings.DBEntry.COL_DATE_COMPLETED
                    ),
                    null, null, null, null, DbSettings.DBEntry.COL_IMPORTANCE
            )
        }else if(prefs.getBoolean(SharedPrefKeys.sortByDeadline, false)){
            cursor = database.query(
                    DbSettings.DBEntry.TABLE,
                    arrayOf(
                            DbSettings.DBEntry.ID,
                            DbSettings.DBEntry.COL_TITLE,
                            DbSettings.DBEntry.COL_CATEGORY,
                            DbSettings.DBEntry.COL_DETAIL,
                            DbSettings.DBEntry.COL_IMPORTANCE,
                            DbSettings.DBEntry.COL_DONE,
                            DbSettings.DBEntry.COL_DATE_COMPLETED,
                            DbSettings.DBEntry.COL_DUE
                    ),
                    null, null, null, null, DbSettings.DBEntry.COL_DUE
            )
        }
        else {
            cursor = database.query(
                    DbSettings.DBEntry.TABLE,
                    arrayOf(
                            DbSettings.DBEntry.ID,
                            DbSettings.DBEntry.COL_TITLE,
                            DbSettings.DBEntry.COL_CATEGORY,
                            DbSettings.DBEntry.COL_DETAIL,
                            DbSettings.DBEntry.COL_IMPORTANCE,
                            DbSettings.DBEntry.COL_DONE,
                            DbSettings.DBEntry.COL_DATE_COMPLETED,
                            DbSettings.DBEntry.COL_DUE
                    ),
                    null, null, null, null, null
            )
        }

        while (cursor.moveToNext()) {
            val cursorId = cursor.getColumnIndex(DbSettings.DBEntry.ID)
            val cursorTitle = cursor.getColumnIndex(DbSettings.DBEntry.COL_TITLE)
            val cursorCategory = cursor.getColumnIndex(DbSettings.DBEntry.COL_CATEGORY)
            val cursorDetail = cursor.getColumnIndex(DbSettings.DBEntry.COL_DETAIL)
            val cursorDue = cursor.getColumnIndex(DbSettings.DBEntry.COL_DUE)
            val cursorImportance = cursor.getColumnIndex(DbSettings.DBEntry.COL_IMPORTANCE)
            val cursorDone = cursor.getColumnIndex(DbSettings.DBEntry.COL_DONE)
            val cursorDateCompleted = cursor.getColumnIndex(DbSettings.DBEntry.COL_DATE_COMPLETED)
            newFavourites.add(
                    ToDo(
                            cursor.getLong(cursorId),
                            cursor.getString(cursorTitle),
                            cursor.getString(cursorCategory),
                            cursor.getString(cursorDetail),
                            cursor.getInt(cursorImportance),
                            cursor.getInt(cursorDone),
                            cursor.getLong(cursorDateCompleted),
                            cursor.getLong(cursorDue)
                    )
            )
        }

        cursor.close()
        database.close()
        this._todoList.value = newFavourites
    }

    fun addToDo(title: String, category: String, detail: String, due: Long, importance: Int) {
        val database: SQLiteDatabase = _todoDBHelper.writableDatabase
        val values = ContentValues()
        values.put(DbSettings.DBEntry.COL_TITLE, title)
        values.put(DbSettings.DBEntry.COL_CATEGORY, category)
        values.put(DbSettings.DBEntry.COL_DETAIL, detail)
        values.put(DbSettings.DBEntry.COL_DUE, due)
        values.put(DbSettings.DBEntry.COL_IMPORTANCE, importance)
        values.put(DbSettings.DBEntry.COL_DONE, 0)
        values.put(DbSettings.DBEntry.COL_DATE_COMPLETED, 0)
        val id = database.insertWithOnConflict(
                DbSettings.DBEntry.TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        )
        database.close()

        var todoList: ArrayList<ToDo>? = this._todoList.value
        if (todoList == null) {
            todoList = ArrayList()
        }

        todoList.add(
                ToDo(
                        id,
                        title,
                        category,
                        detail,
                        importance,
                        0,
                        0,
                        due
                )
        )
        this._todoList.value = todoList
    }

    fun updatedone(id: Long, done: Int){
        val database: SQLiteDatabase = _todoDBHelper.writableDatabase
        var content = ContentValues()
        if(done == 0){
            content.put(DbSettings.DBEntry.COL_DONE, 1)
            content.put(DbSettings.DBEntry.COL_DATE_COMPLETED, Date().time)
        }else{
            content.put(DbSettings.DBEntry.COL_DONE, 0)
            content.put(DbSettings.DBEntry.COL_DATE_COMPLETED, 0)
        }
        database.update(DbSettings.DBEntry.TABLE, content, DbSettings.DBEntry.ID + " = " + id.toString(), null)
        database.close()
    }

    fun editTodo(id: Long, title: String, category: String, detail: String, due: Long, importance: Int){
        val database: SQLiteDatabase = _todoDBHelper.writableDatabase
        val values = ContentValues()
        values.put(DbSettings.DBEntry.COL_TITLE, title)
        values.put(DbSettings.DBEntry.COL_CATEGORY, category)
        values.put(DbSettings.DBEntry.COL_DETAIL, detail)
        values.put(DbSettings.DBEntry.COL_DUE, due)
        values.put(DbSettings.DBEntry.COL_IMPORTANCE, importance)

        database.update(DbSettings.DBEntry.TABLE, values, DbSettings.DBEntry.ID + " = " + id.toString(), null)
        database.close()
    }

    fun removeToDo(id: Long) {
        val database: SQLiteDatabase = _todoDBHelper.writableDatabase
        database.delete(
                DbSettings.DBEntry.TABLE,
                DbSettings.DBEntry.ID + " = ?",
                arrayOf(id.toString())
        )
        database.close()

        var index = 0
        val todos: ArrayList<ToDo>? = this._todoList.value
        if (todos != null) {
            for (i in 0 until todos.size) {
                if (todos[i].id == id) {
                    index = i
                }
            }
            todos.removeAt(index)
            this._todoList.value = todos
        }
    }


}