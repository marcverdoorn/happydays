package com.example.tutorialapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.OutputStreamWriter
import java.lang.Exception

class add_task : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        val tasks = get_tasks()
        val textView = findViewById<TextView>(R.id.textView7)
        textView.text = tasks
    }

    fun add_task(view: View){
        val data = findViewById<EditText>(R.id.editTextTextPersonName).toString()
        save_task(data)
    }

    fun save_task(data: String){
        try {
            val fos = openFileOutput("tasks.txt", Context.MODE_APPEND)
            fos.write(data.toByteArray())
            fos.close()
            Toast.makeText(baseContext, "saving done", Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            Toast.makeText(baseContext, "saving task failed", Toast.LENGTH_SHORT).show()
        }

    }

    fun get_tasks(): String{
        try {
            val tasks = File("files/tasks.txt").readBytes().toString()

            return tasks
        }catch (e: Exception){
            Toast.makeText(baseContext, e.toString(), Toast.LENGTH_LONG).show()
            return e.toString()
        }
    }
}