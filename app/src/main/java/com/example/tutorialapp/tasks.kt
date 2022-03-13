package com.example.tutorialapp

import android.app.ActionBar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import android.util.TypedValue
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_tasks.*

class tasks : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)
        for (i in (0..30)){
            add_task_view(i, "throw out the garbage")
        }
    }

    fun add_task(view: View){
        val intent = Intent(this, add_task::class.java).apply {  }
        startActivity(intent)
    }

    fun delete_task(task: String){
        Toast.makeText(baseContext, task, Toast.LENGTH_SHORT).show()
    }

    fun add_task_view(color: Int, text: String){
        val taskview: TextView = TextView(this)
        taskview.textSize = 20f
        taskview.text = text
        taskview.textAlignment = View.TEXT_ALIGNMENT_CENTER
        taskview.setPadding(10,50,10,50)

        taskview.setBackgroundColor(Color.rgb(230-color*5, 19+color*3, color*6))
        task_layout.addView(taskview)

        val done: Button = Button(this)
        done.text = "DONE"
        done.width = 120
        done.height = 60
        task_layout.addView(done)

        val delete: Button = Button(this)
        delete.text = "Delete"
        delete.height = 60
        delete.tag = color.toString()
        delete.setOnClickListener(View.OnClickListener {
            delete_task(color.toString())
        })
        task_layout.addView(delete)

    }
}