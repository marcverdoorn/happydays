@file:Suppress("UnusedImport")

package com.example.tutorialapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.File
import java.io.InputStream
import java.lang.Exception
import android.content.SharedPreferences
import java.nio.charset.Charset



const val EXTRA_user = "com.example.tutorialapp.user"
const val EXTRA_apikey = "com.example.tutorialapp.apikey"
val test = "slava ukrain"

class MainActivity : AppCompatActivity() {
    private val sharedPrefFile = "hpd_api_data"
    private var api_key: String = ""
    private var username: String = ""
    private var loggedin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            get_api_config()
        }catch (e: Exception){
            Toast.makeText(baseContext, "Oops something went wrong, try to log in", Toast.LENGTH_LONG).show()
        }

        val textView = findViewById<TextView>(R.id.textView4)

        if (api_key == "default" || api_key == "" || username == "default" || username == ""){
            textView.text = "No user logged in"
            loggedin = false
        }else{
            textView.text = "Hello $username"
            loggedin = true
        }

        if (loggedin){
            apireq(username, api_key, "score", "scoreview")
            apireq(username, api_key, "friends", "friendlist")
        }
    }

    fun sendMessage(view: View){
        //val message = "empty"
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            //putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)
    }

    fun apireq(user: String, apiKey: String, cat: String, output_placement: String){
        var strResp = ""
        val queue = Volley.newRequestQueue(this)
        val url = "https://shappie.net/hpdGetInfo.php?username=$user&val=$cat"
        val requestBody = "accesscode=$apiKey"
        val stringReq : StringRequest=
            object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    strResp = response.toString()
                    if(strResp == "no data"){
                        loggedin = false
                        place_data_ouput("loginstat", "Please log in!")
                        Toast.makeText(baseContext, "Please log in!", Toast.LENGTH_LONG).show()
                        if (cat == "score"){
                            val savedData = get_saved_data(cat)
                            place_data_ouput(output_placement, savedData)
                        }
                    }else{
                        loggedin = true
                        place_data_ouput("loginstat", "You're logged in")
                        place_data_ouput(output_placement, strResp)
                        if (cat == "score" || cat == "pref"){
                            save_data(cat, strResp)
                        }
                    }
                },
                Response.ErrorListener { error ->
                    strResp = "0"
                    place_data_ouput("loginstat", "Check internet connection")
                    if (cat == "score"){
                        val savedData = get_saved_data(cat)
                        place_data_ouput(output_placement, savedData)
                    }
                }){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)
    }

    fun newaccount(view: View){
        val openURL = Intent(android.content.Intent.ACTION_VIEW)
        openURL.data = Uri.parse("https://shappie.net/hpdNewUser.php")
        startActivity(openURL)
    }

    fun refresh(view: View){
        try {
            get_api_config()
        }catch (e: Exception){
            Toast.makeText(baseContext, "Oops something went wrong, try to log in", Toast.LENGTH_LONG).show()
        }

        val textView = findViewById<TextView>(R.id.textView4)

        if (api_key == "default" || api_key == "" || username == "default" || username == ""){
            textView.text = "No user logged in"
            loggedin = false
        }else{
            textView.text = "Hello $username"
            loggedin = true
        }

        if (loggedin){
            apireq(username, api_key, "score", "scoreview")
            apireq(username, api_key, "friends", "friendlist")
        }
    }

    fun addFriends(view: View){
        if (loggedin){
            val intent = Intent(this, AddFriendActivity::class.java).apply {
                putExtra(EXTRA_user, username)
                putExtra(EXTRA_apikey, api_key)
            }
            startActivity(intent)
        }else{
            Toast.makeText(baseContext, "Login first", Toast.LENGTH_LONG).show()
        }

    }


    fun get_api_config(){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        api_key = sharedPreferences.getString("api_key","default").toString()
        username = sharedPreferences.getString("username","default").toString()
        //Toast.makeText(baseContext, "key $api_key name $username", Toast.LENGTH_SHORT).show()
    }

    fun place_data_ouput(place: String, data: String){
        if (place == "scoreview"){
            val textView = findViewById<TextView>(R.id.textView)
            textView.text = "Your score is $data"
        }else if (place == "loginstat"){
            val textView = findViewById<TextView>(R.id.textView5)
            textView.text = data
        }else if (place == "friendlist"){
            val textView = findViewById<TextView>(R.id.textView6)
            val output = data.replace(";", "\n")
            textView.text = output
        }
    }

    fun get_saved_data(datacategory: String): String{
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val requested_data = sharedPreferences.getString(datacategory, "default").toString()
        return requested_data
    }

    fun save_data(datacategory: String, data: String){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(datacategory, data)
        editor.apply()
        editor.commit()
    }
}
