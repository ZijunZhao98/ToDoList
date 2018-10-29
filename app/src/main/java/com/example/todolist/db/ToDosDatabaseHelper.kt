package com.example.todolist.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ToDosDatabaseHelper(context: Context): SQLiteOpenHelper(context, DbSettings.DB_NAME, null, DbSettings.DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE " + DbSettings.DBEntry.TABLE + " ( " +
                DbSettings.DBEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbSettings.DBEntry.COL_TITLE + " TEXT NOT NULL, " +
                DbSettings.DBEntry.COL_CATEGORY + " TEXT NOT NULL, " +
                DbSettings.DBEntry.COL_DETAIL + " TEXT NOT NULL, " +
                DbSettings.DBEntry.COL_DUE + " DATE NOT NULL, " +
                DbSettings.DBEntry.COL_IMPORTANCE + " INTEGER NOT NULL, " +
                DbSettings.DBEntry.COL_DONE + " INTEGER NOT NULL, " +
                DbSettings.DBEntry.COL_DATE_COMPLETED + " DATE NOT NULL);"

        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + DbSettings.DBEntry.TABLE)
        onCreate(db)
    }
}