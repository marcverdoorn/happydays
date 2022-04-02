package com.example.tutorialapp

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_tasks.*
import java.lang.Exception
import java.nio.charset.Charset

data class contender(val name: String, val score: Int){}

class scoreboard : AppCompatActivity() {
    private val sharedPrefFile = "hpd_api_data"
    private var api_key: String = ""
    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)
        get_api_config()
        get_friendscores(username, api_key)
    }

    fun add_score_view(index: Int, data: contender){
        val taskview: TextView = TextView(this)
        taskview.textSize = 20f
        taskview.text = "${data.name} has ${data.score} points"
        taskview.textAlignment = View.TEXT_ALIGNMENT_CENTER
        taskview.setPadding(10,50,10,50)
        taskview.setBackgroundColor(Color.rgb(index*6, 19+index*3, 230-index*5 ))
        task_layout.addView(taskview)
    }

    fun get_friendscores(username: String, api_key: String){
        val queue = Volley.newRequestQueue(this)
        val url = "https://shappie.net/hpdFriendScore.php?username=$username"
        val requestBody = "accesscode=$api_key"
        val stringReq : StringRequest =
            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    val strResp = response.toString()
                    if(strResp == "no data" || strResp == "no acces"){
                        Toast.makeText(baseContext, "Please log in!", Toast.LENGTH_LONG).show()
                    }else{
                        display_scores(strResp)
                    }
                },
                Response.ErrorListener { error ->
                    val strResp = "0"

                }){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)
    }

    fun display_scores(data: String){
        val raw_list = data.split(";")
        val contenders = mutableListOf<contender>()
        var i = 0
        while (i < (raw_list.size - 1)){
            contenders.add(contender(raw_list[i], raw_list[i+1].toInt()))
            i += 2
        }
        contenders.sortByDescending { it.score }
        var x = 0
        while (x < (contenders.size - 1)){
            add_score_view(x, contenders[x])
            x++
        }
    }

    fun get_api_config(){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        api_key = sharedPreferences.getString("api_key","default").toString()
        username = sharedPreferences.getString("username","default").toString()
    }
}