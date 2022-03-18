package com.example.tutorialapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder

class add_task : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        val tasks = get_tasks()
        val textView = findViewById<TextView>(R.id.textView7)
        textView.text = tasks
    }

    fun add_task(view: View){
        val data = findViewById<EditText>(R.id.editTextTextPersonName).text.toString()
        save_task(data)
        val tagdata = view.tag.toString()
        Toast.makeText(baseContext, tagdata, Toast.LENGTH_LONG).show()
    }

    fun save_task(data: String){
        try {
            val fos: FileOutputStream = openFileOutput("tasks.txt", Context.MODE_PRIVATE)
            fos.write(data.toByteArray())
            fos.close()
            Toast.makeText(baseContext, "saving done", Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            Toast.makeText(baseContext, "saving task failed", Toast.LENGTH_SHORT).show()
        }

    }

    fun get_tasks(): String{
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
            return tasks
        }catch (e: Exception){
            Toast.makeText(baseContext, e.toString(), Toast.LENGTH_LONG).show()
            return e.toString()
        }
    }
}
