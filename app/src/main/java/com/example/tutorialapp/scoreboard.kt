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
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_tasks.*
import java.lang.Exception
import java.nio.charset.Charset

data class contender(val name: String, val score: Int){}
data class rgb(val red: Int, val green: Int, val blue: Int){}

class scoreboard : AppCompatActivity() {
    private val sharedPrefFile = "hpd_api_data"
    private var username: String = ""
    private var api_key: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)
        get_api_config()
        try {
            get_friendscores(username, api_key)
        }catch (e: Exception){
            Toast.makeText(baseContext, "Oops something went wrong", Toast.LENGTH_SHORT).show()
        }

        val textView = findViewById<TextView>(R.id.textView8)
        textView.text = "Your score: ${get_saved_data("score")}"
    }

    fun add_score_view(color: rgb, data: contender){
        val taskview: TextView = TextView(this)
        taskview.textSize = 20f
        taskview.text = "${data.name} has ${data.score} points"
        taskview.textAlignment = View.TEXT_ALIGNMENT_CENTER
        taskview.setPadding(10,50,10,50)
        taskview.setBackgroundColor(Color.rgb(color.red, color.green, color.blue ))
        taskview.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete friend")
            builder.setMessage("Do you want to delete ${data.name}?")
            builder.setPositiveButton("YES"){
                dialogInterface, which ->
                Toast.makeText(baseContext, "Deleting ${data.name}", Toast.LENGTH_SHORT).show()
                try{
                    delete_friend(data.name)
                }catch (e: Exception){
                    Toast.makeText(baseContext, "Deleting failed", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("NO"){
                dialogInterface, which ->
                Toast.makeText(baseContext, "${data.name} thanks you!", Toast.LENGTH_SHORT).show()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        })
        task_layout.addView(taskview)
        val spaceview: TextView = TextView(this)
        spaceview.text = ""
        spaceview.textSize = 10f
        task_layout.addView(spaceview)
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
        contenders.add(contender(username, get_saved_data("score").toInt()))
        contenders.sortByDescending { it.score }
        var x = 0
        val green = rgb(47, 212, 99)
        val red = rgb(212, 47, 47)
        while (x < (contenders.size - 0)){
            add_score_view(rgb(
                (green.red + (red.red-green.red)/contenders.size * x),
                (green.green + (red.green-green.green)/contenders.size * x),
                (green.blue + (red.blue-green.blue)/contenders.size * x)
            ), contenders[x])
            x++
        }
    }

    fun delete_friend(friend: String){
        val queue = Volley.newRequestQueue(this)
        val url = "https://shappie.net/hpdDelFriend.php?username=$username&friend=$friend"
        val requestBody = "accesscode=$api_key"
        val stringReq : StringRequest =
            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    val strResp = response.toString()
                    if(strResp == "no data" || strResp == "no acces"){
                        Toast.makeText(baseContext, "Please log in!", Toast.LENGTH_LONG).show()
                    }else if (strResp == "done"){
                        Toast.makeText(baseContext, "Friend deleted", Toast.LENGTH_LONG).show()
                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(getIntent())
                        overridePendingTransition(0, 0)
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(baseContext, "Deleting friend failed", Toast.LENGTH_LONG).show()
                }){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)
    }

    fun get_api_config(){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        api_key = sharedPreferences.getString("api_key","default").toString()
        username = sharedPreferences.getString("username","default").toString()
    }

    fun get_saved_data(datacategory: String): String{
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val requested_data = sharedPreferences.getString(datacategory, "default").toString()
        return requested_data
    }
}