package com.example.tutorialapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import java.net.URLEncoder
import java.net.URI
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.nio.charset.Charset
import android.widget.Toast
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class DisplayMessageActivity : AppCompatActivity() {
    private val sharedPrefFile = "hpd_api_data"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        //val message = intent.getStringExtra(EXTRA_MESSAGE)
    }

    fun login(view: View){
        val username = findViewById<EditText>(R.id.editTextTextPersonName2).text.toString()
        val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()
        var apikey = "empty"
        val url = "https://shappie.net/hpdLogin.php"
        val requestBody = "username=$username&password=$password"
        val queue = Volley.newRequestQueue(this)
        val textView = findViewById<TextView>(R.id.textView3)

        val stringReq : StringRequest=
            object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    var strResp = response.toString()
                    textView.text = react(strResp, username)
                    apikey = strResp
                },
            Response.ErrorListener { error ->
                    var strResp = "no connection"
                    textView.text = strResp
            }){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)



    }

    fun react(result: String, username: String): String{
        val message: String
        val apiKey = result
        if (result == "empty"){
            message = "Oeps something went wrong"
        }else if (result == "0"){
            message = "Wrong password"
        }else if (result == "00"){
            message = "User doens't exist"
        }else {
            message = "successful login"
            saveKey(apiKey, username)
        }
        return message
    }

    fun saveKey(api_key: String, username: String) {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("api_key", api_key)
        editor.putString("username", username)
        editor.apply()
        editor.commit()
    }

}