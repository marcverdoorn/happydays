package com.example.tutorialapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_tasks.*

class tasks : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        val taskview = TextView(this)
        taskview.textSize = 20f
        taskview.text = "Do a task"
        task_layout.addView(taskview)
    }
}