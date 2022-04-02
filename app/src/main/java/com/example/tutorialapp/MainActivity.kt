@file:Suppress("UnusedImport")

package com.example.tutorialapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.nio.charset.Charset
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*


const val EXTRA_user = "com.example.tutorialapp.user"
const val EXTRA_apikey = "com.example.tutorialapp.apikey"


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
            score_reset()
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
            get_friendscores(username, api_key)
        }
    }

    fun sendMessage(view: View){
        //val message = "empty"
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            //putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)
    }


    fun show_tasks(view: View){
        val intent = Intent(this, tasks::class.java)
        startActivity(intent)
    }

    fun score_board(view: View){
        val intent = Intent(this, scoreboard::class.java)
        startActivity(intent)
    }

    fun update_score(score: Int){
        val stored_score = get_saved_data("score")
        if (stored_score.toInt() > score){
            update_db(username, api_key, "score", stored_score)
            place_data_ouput("scoreview", stored_score)
        }else{
            save_data("score", score.toString())
        }
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
                        if (cat == "score"){
                            update_score(strResp.toInt())
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

    fun get_friendscores(username: String, api_key: String){

        val queue = Volley.newRequestQueue(this)
        val url = "https://shappie.net/hpdFriendScore.php?username=$username"
        val requestBody = "accesscode=$api_key"
        val stringReq : StringRequest=
            object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    val strResp = response.toString()
                    if(strResp == "no data"){
                        Toast.makeText(baseContext, "Please log in!", Toast.LENGTH_LONG).show()
                    }else{
                        place_data_ouput("friendlist", strResp)
                    }
                },
                Response.ErrorListener { error ->
                    val strResp = "0"
                    place_data_ouput("loginstat", "Check internet connection")
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
            score_reset()
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
            get_friendscores(username, api_key)
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

    fun score_reset(){
        val week = Calendar.getInstance(TimeZone.getTimeZone("UTC+1")).get(Calendar.WEEK_OF_YEAR)
        val year = Calendar.getInstance(TimeZone.getTimeZone("UTC+1")).get(Calendar.YEAR)
        val stored_week = get_saved_data("week")
        val stored_year = get_saved_data("year")
        if (stored_week == "default"){
            save_data("week", week.toString())
        }
        if (stored_year == "default"){
            save_data("year", week.toString())
        }
        if (stored_week != "default" && stored_year != "default"){
            if (week - stored_week.toInt() > 0 && year - stored_year.toInt() == 0){
                reset_score_database(username, api_key, "score", "0", year, week, false)
            }else if (week - stored_week.toInt() < 0 || year - stored_year.toInt() > 0){
                reset_score_database(username, api_key, "score", "0", year, week, true)
            }
        }else{
            Toast.makeText(baseContext, "data not found",Toast.LENGTH_LONG).show()
        }
    }

    fun reset_score_database(user: String, apiKey: String, cat: String, data: String, year: Int, week: Int, Y_reset: Boolean){
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
                    }else if (Y_reset){
                        save_data("week", week.toString())
                        save_data("year", year.toString())
                        save_data("score", "0")
                    }else{
                        save_data("week", week.toString())
                        save_data("score", "0")
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(baseContext, "Please connect to internet to update score", Toast.LENGTH_LONG).show()
                }){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)
    }

    fun update_db(user: String, apiKey: String, cat: String, data: String){
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
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(baseContext, "Something went wrong. Please check internet connection", Toast.LENGTH_LONG).show()
                }){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)
    }
}
