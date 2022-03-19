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
        val stringBuilder:StringBuilder = StringBuilder()
        val tasklist = convert_to_array(tasks)
        for(i in tasklist){
            stringBuilder.append(i + "\n")
        }
        textView.text = stringBuilder
    }

    fun add_task(view: View){
        val editText = findViewById<EditText>(R.id.editTextTextPersonName)
        val data = editText.text.toString()
        editText.setText("")
        if (data.trim().length > 0 && !data.contains(";")){
            save_task(data)
        }

    }

    fun save_task(data: String){
        try {
            val old_tasks = get_tasks()
            val new_tasks:String
            if(old_tasks != ""){
                new_tasks = old_tasks + data + ';'
            }else{
                new_tasks = data + ';'
            }
            val fos: FileOutputStream = openFileOutput("tasks.txt", Context.MODE_PRIVATE)
            fos.write(new_tasks.toByteArray())
            fos.close()
            //Toast.makeText(baseContext, "saving done", Toast.LENGTH_SHORT).show()
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
            return ""
        }
    }

    fun convert_to_array(data: String): List<String>{
        val list = data.split(";")
        return list
    }
}
