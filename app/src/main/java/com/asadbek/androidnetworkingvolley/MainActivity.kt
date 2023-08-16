package com.asadbek.androidnetworkingvolley

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.asadbek.androidnetworkingvolley.databinding.ActivityMainBinding
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var requestQueue: RequestQueue
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestQueue = Volley.newRequestQueue(this) // Volleyni bir marotaba tanitib olish

        if(isHaveNetwork()){
            binding.txt.text = "Internet mavjud"
            fetchImage(binding.image,"https://img.freepik.com/free-vector/bird-colorful-logo-gradient-vector_343694-1365.jpg")

            fetchObject("http://ip.jsontest.com/",binding.txt)
        }else{
            binding.txt.text = "Internet mavjud emas!"
        }


    }

    // volley orqali internetdan surat olib kelish
    private fun fetchImage(imageView: ImageView,url: String) {
        val imageRequest = ImageRequest(url,object : Response.Listener<Bitmap>{
            override fun onResponse(response: Bitmap?) { // Bitmap - rasmni saqlab olish uchun tip
                imageView.setImageBitmap(response)
            }
        },0, // rasm eni o`lchami
            0, // rasm bo`yi o`lchami
            ImageView.ScaleType.CENTER_CROP, // imageview ga o`rnashish xolati
            Bitmap.Config.ARGB_8888, // bitmap configi ranglar uchun
            object :Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Toast.makeText(this@MainActivity, "$error", Toast.LENGTH_SHORT).show()
            }
        })
        requestQueue.add(imageRequest) // volleyni requestimage ni ishga tushirish
    }


    // volley orqali json malumotni olish berilgan url dan
    private fun fetchObject(url:String,textView: TextView){
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, // ishlash xolati GET - malumot olib kelish
            url, // joriy urldan olib keladi
            null, // null ?
            {               // response
                val str = it.getString("ip") // serverdagi string ozgaruvchi nomi -> ip // shuning malumotnini olib keladi
                binding.txt.text = str
            },{             // error

            })

        requestQueue.add(jsonRequest) // json So`rov yuborish volley orqali
    }

    // volley orqali json array ni urldan olib kelish
    // misol: http://cbu.uz/uzc/arkhiv-kursov-valyut/json/
    private fun fetchArray(url:String,textView: TextView){
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            {  response ->// response listener
                val str = response.toString() // agar json formatda olib kelsa uni listga ololamiz
                binding.txt.text = str

                // huddi shu yerda jsonarrayni list ga otkazb olishimiz va ekranga tartib bilan chiqarishimiz mumkin
            },
            { // error listener
                // malumot kelmaganda error ga uchrashi
                Toast.makeText(this, "${it.printStackTrace()}", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(jsonArrayRequest)
    }


    //  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/> = internet bor yoki yoqligini tekshirib beradi
    fun isHaveNetwork():Boolean{
        val connectiveManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M){ // api 23 dan yuqori bo`lgan qurilmalarga
            val activeNetwork = connectiveManager.activeNetwork // aktiv holatdagi tarmoq
            val networkCapabilities = connectiveManager.getNetworkCapabilities(activeNetwork) // tarmoq sifati
            return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)// tarmoq sifati null bolmasa true
        }else{ // api 23 dan kichiklar uchun
            val activeNetworkInfo = connectiveManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }




}