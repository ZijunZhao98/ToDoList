package com.example.todolist

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import com.example.todolist.model.ToDo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.todo_display.view.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var todoModel: ToDoMethods
    private var todoList: ArrayList<ToDo> = ArrayList()
    private var adapter: ToDoAdapter = ToDoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        todoModel = ViewModelProviders.of(this).get(ToDoMethods::class.java)

        fab.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            startActivity(intent)
        }

        val observer = android.arch.lifecycle.Observer<ArrayList<ToDo>> {
            recyclerView.adapter = adapter
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return todoList.size
                }

                override fun getNewListSize(): Int {
                    return it!!.size
                }

                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    return todoList[p0].id == todoList[p1].id
                }

                override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                    return todoList[p0] == todoList[p1]
                }
            })
            result.dispatchUpdatesTo(adapter) // update the adapter
            todoList = it!!
        }

        val prefs = getSharedPreferences("$packageName.${SharedPrefKeys.SORT_PREFERENCES_FILE}", Context.MODE_PRIVATE)

        todoModel.getToDos(prefs).observe(this, observer)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_title -> {
                setPreference("title")
                true
            }
            R.id.menu_importance -> {
                setPreference("importance")
                true
            }
            R.id.menu_deadline -> {
                setPreference("deadline")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setPreference(key: String){
        val sharedPreferences = getSharedPreferences("$packageName.${SharedPrefKeys.SORT_PREFERENCES_FILE}", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if(key == "title"){
            editor.putBoolean(SharedPrefKeys.sortByTitle, true)
            editor.putBoolean(SharedPrefKeys.sortByDeadline, false)
            editor.putBoolean(SharedPrefKeys.sortByImportance, false)
        }else if(key == "deadline"){
            editor.putBoolean(SharedPrefKeys.sortByTitle, false)
            editor.putBoolean(SharedPrefKeys.sortByDeadline, true)
            editor.putBoolean(SharedPrefKeys.sortByImportance, false)
        }else if(key == "importance"){
            editor.putBoolean(SharedPrefKeys.sortByTitle, false)
            editor.putBoolean(SharedPrefKeys.sortByDeadline, false)
            editor.putBoolean(SharedPrefKeys.sortByImportance, true)
        }else{
            editor.putBoolean(SharedPrefKeys.sortByTitle, false)
            editor.putBoolean(SharedPrefKeys.sortByDeadline, false)
            editor.putBoolean(SharedPrefKeys.sortByImportance, false)
        }

        editor.apply()
        todoModel.getToDos(sharedPreferences)

    }

    inner class ToDoAdapter: RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ToDoViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.todo_display, p0, false)
            return ToDoViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: ToDoViewHolder, p1: Int) {
            val todo = todoList[p1]
            p0.titleTextView.text = todo.title
            p0.detailTextView.text = todo.detail
            p0.dateTextView.text = Date(todo.due).toString()
            p0.categoryTextView.text = todo.category
            p0.importanceTextView.text = todo.importance.toString()
        }

        override fun getItemCount(): Int {
            return todoList.size
        }

        inner class ToDoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            var titleTextView: TextView = itemView.title
            var dateTextView: TextView = itemView.date
            var detailTextView: TextView = itemView.detail
            var categoryTextView: TextView = itemView.category
            var importanceTextView: TextView = itemView.importance

            init{

                itemView.edit.setOnClickListener{
                    val todo = todoList[adapterPosition]
                    val intent = Intent(itemView.context, ToDoActivity::class.java)
                    intent.putExtra("option","edit")
                    intent.putExtra("id", todo.id)
                    intent.putExtra("title", todo.title)
                    intent.putExtra("date", todo.due)
                    intent.putExtra("detail", todo.detail)
                    intent.putExtra("category", todo.category)
                    intent.putExtra("importance", todo.importance)

                    startActivity(intent)
                }

                itemView.delete.setOnClickListener {
                    val todo = todoList[adapterPosition]
                    todoModel.removeToDo(todo.id)
                }

                itemView.checkbox.setOnClickListener {
                    val todo = todoList[adapterPosition]

                    if(itemView.checkbox.text == ""){
                        todoModel.updatedone(todo.id, 1)
                        itemView.checkbox.text = "√"
                    }else{
                        todoModel.updatedone(todo.id, 0)
                        itemView.checkbox.text = ""
                    }
                }
            }
        }
    }
}
