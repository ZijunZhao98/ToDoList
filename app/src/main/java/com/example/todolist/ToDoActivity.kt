package com.example.todolist

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_to_do.*
import kotlinx.android.synthetic.main.todo_display.*
import java.util.*

class ToDoActivity :  AdapterView.OnItemSelectedListener, AppCompatActivity() {

    private lateinit var todoModel: ToDoMethods
    private val importanceArray = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    private var selectedImportance = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)

        todoModel = ViewModelProviders.of(this).get(ToDoMethods::class.java)

        //create a drop down list
        input_importance!!.setOnItemSelectedListener(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, importanceArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        input_importance.adapter = adapter

        var intent: Intent = intent
        var option = intent.getStringExtra("option")
        if(option == "edit"){
            input_title.setText(intent.getStringExtra("title"))
            input_category.setText(intent.getStringExtra("category"))
            input_detail.setText(intent.getStringExtra("detail"))
            var date: Calendar = Calendar.getInstance()
            date.setTimeInMillis(intent.getLongExtra("date", 0))
            selectedImportance = intent.getIntExtra("importance", 0)
            input_deadline.init(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), null);

            create_btn.text = "EDIT"
        }else{
            create_btn.text = "CREATE"
        }

        input_category.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
        }

        if(intent.getIntExtra("change", 0) == 1){
            input_category.setText(intent.getStringExtra("choose_category"))
        }


        create_btn.setOnClickListener {
            if(option == "edit"){
                var id = intent.getLongExtra("id", 0)
                Log.d("mydebug", selectedImportance.toString())
                todoModel.editTodo(id, input_title.text.toString(),input_category.text.toString(),input_detail.text.toString(),getTime(), selectedImportance)
            }else{
                todoModel.addToDo(input_title.text.toString(),input_category.text.toString(),input_detail.text.toString(),getTime(), selectedImportance)
            }

            //jump back to main page
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        selectedImportance = importanceArray[position]
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }


    private fun getTime():Long{
        val day = input_deadline.dayOfMonth
        val month = input_deadline.month
        val year = input_deadline.year
        val calendar : Calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        return calendar.timeInMillis
    }
}
