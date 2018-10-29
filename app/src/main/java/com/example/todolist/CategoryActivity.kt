package com.example.todolist

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_category.*


class CategoryActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        school.setOnClickListener {
            jump("school")
        }

        homework.setOnClickListener {
            jump("homework")
        }

        work.setOnClickListener {
            jump("work")
        }


    }

    private fun jump(category: String){
        val intent = Intent(this, ToDoActivity::class.java)
        intent.putExtra("change", 1)
        intent.putExtra("choose_category", category)

        startActivity(intent)
    }
}
