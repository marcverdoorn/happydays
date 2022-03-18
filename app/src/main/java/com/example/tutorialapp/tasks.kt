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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder

class tasks : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)
        val tasks = get_task_list()
        if (tasks != null){
            for(i in tasks){
                add_task_view(tasks.lastIndexOf(i), i)
            }
        }
    }

    fun add_task(view: View){
        val intent = Intent(this, add_task::class.java).apply {  }
        startActivity(intent)
    }

    fun delete_task(index: Int){
        val tasknum = index +1
        Toast.makeText(baseContext, "Deleting task $tasknum", Toast.LENGTH_SHORT).show()
    }

    fun done_task(index: Int){
        val tasknum = index +1
        Toast.makeText(baseContext, "Finished task $tasknum", Toast.LENGTH_SHORT).show()
    }

    fun get_task_list():List<String>?{
        try {
            val file = openFileInput("tasks.txt")
            val inputStreamReader : InputStreamReader = InputStreamReader(file)
            val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder: StringBuilder = StringBuilder()
            var text: String? = null

            while ({text = bufferedReader.readLine(); text}() != null){
                stringBuilder.append(text)
            }
            val tasks = stringBuilder.toString()
            val list = tasks.split(";")
            return list.dropLast(1)
        }catch (e: Exception){
            Toast.makeText(baseContext, "No tasks yet", Toast.LENGTH_LONG).show()
            return null
        }

    }

    fun add_task_view(index: Int, text: String){
        val taskview: TextView = TextView(this)
        taskview.textSize = 20f
        taskview.text = text
        taskview.textAlignment = View.TEXT_ALIGNMENT_CENTER
        taskview.setPadding(10,50,10,50)
        taskview.setBackgroundColor(Color.rgb(230-index*5, 19+index*3, index*6))
        task_layout.addView(taskview)

        val done: Button = Button(this)
        done.text = "DONE"
        done.width = 120
        done.height = 60
        done.setOnClickListener(View.OnClickListener {
            done_task(index)
        })
        task_layout.addView(done)

        val delete: Button = Button(this)
        delete.text = "Delete"
        delete.height = 60
        delete.tag = index.toString()
        delete.setOnClickListener(View.OnClickListener {
            delete_task(index)
        })
        task_layout.addView(delete)

    }
}
