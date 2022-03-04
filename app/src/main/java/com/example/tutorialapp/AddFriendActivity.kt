package com.example.tutorialapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.LocalTime

class AddFriendActivity : AppCompatActivity() {
    private var api_key: String = ""
    private var username: String = ""
    private var time = LocalTime.now().toSecondOfDay()
    private var day = LocalDateTime.now().dayOfYear
    private var last_time = LocalTime.now().toSecondOfDay() - 60
    private var own_code = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        username = intent.getStringExtra(EXTRA_user).toString()
        api_key = intent.getStringExtra(EXTRA_apikey).toString()

    }

    fun newFcode(view: View){
        time = LocalTime.now().toSecondOfDay()
        if (time - last_time >= 60 || LocalDateTime.now().dayOfYear - day >= 1){
            last_time = time
            val code = generateFriendCode()
            val textView = findViewById<TextView>(R.id.textView2)
            textView.text = "Generating code"
            apireq(username, api_key, "Fcode", code)
        }else{
            Toast.makeText(baseContext, "To soon for new code", Toast.LENGTH_SHORT).show()
        }
    }

    fun AddFriend(view: View){
        val Fcode = findViewById<EditText>(R.id.friendcodeinput).toString()
        if(Fcode == own_code){
            Toast.makeText(baseContext, "This is your own code, please enter another code", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(baseContext, own_code, Toast.LENGTH_SHORT).show()
        }
    }

    fun apireq(user: String, apiKey: String, cat: String, data: String){
        var strResp = ""
        val queue = Volley.newRequestQueue(this)
        val url = "https://shappie.net/hpdChangeInfo.php?username=$user&val=$data&cat=$cat"
        val requestBody = "accesscode=$apiKey"
        val stringReq : StringRequest =
            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    strResp = response.toString()
                    if(strResp == "no data" || strResp == "error"){
                        Toast.makeText(baseContext, "Something went wrong. Please log in!", Toast.LENGTH_LONG).show()
                        showFcode("Try again later")
                    }else if(strResp == "done" && cat == "Fcode"){
                        showFcode(data)
                        Toast.makeText(baseContext, "Code ready for use", Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(baseContext, "Something went wrong. Please check internet connection", Toast.LENGTH_LONG).show()
                    showFcode("Try again later")
                }){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)
    }

    fun generateFriendCode(): String{
        var rancode = ""
        val numbers = Array<Int>(8, { i -> (97..122).random() })
        for ( i in numbers){
            rancode += i.toChar()
        }
        return rancode
    }

    fun showFcode(code: String){
        val textView = findViewById<TextView>(R.id.textView2)
        textView.text = code
        own_code = code
    }
}