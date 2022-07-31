package com.cpe.infofarm


import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class Weather : AppCompatActivity() {

    private val client = OkHttpClient()
    var arrayListDetails:ArrayList<Model> = ArrayList()
    private lateinit var place: TextView
    private lateinit var temp: TextView
    private lateinit var skyStatus: TextView
    private lateinit var sixWeather: TextView
    private lateinit var sixRainView: TextView
    private lateinit var sixTimeView: TextView
    private lateinit var twelveTimeView: TextView
    private lateinit var threeTimeView: TextView
    private lateinit var twelveRainView: TextView
    private lateinit var threeRainView: TextView
    private lateinit var sixIconView: ImageView
    private lateinit var twelveWeather: TextView
    private lateinit var twelveIconView: ImageView
    private lateinit var threeWeather: TextView
    private lateinit var threeIconView: ImageView
    private lateinit var weatherIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        val classifybtn: Button = findViewById (R.id.button_pest)
        val pricebtn: Button = findViewById (R.id.button_price)

        classifybtn.setOnClickListener{
            val intent = Intent(this, Classify::class.java)
            startActivity(intent)
        }

        pricebtn.setOnClickListener{
            val intent = Intent(this, Price::class.java)
            startActivity(intent)
        }

        temp = findViewById(R.id.main_temp)
        place = findViewById(R.id.location)
        skyStatus = findViewById(R.id.weather_description)
        weatherIcon = findViewById(R.id.main_temp_icn)
        sixWeather = findViewById(R.id.sixWeather)
        sixRainView = findViewById(R.id.sixRain)
        twelveRainView = findViewById(R.id.twelveRain)
        threeRainView = findViewById(R.id.threeRain)
        sixIconView = findViewById(R.id.sixIcon)
        twelveWeather = findViewById(R.id.twelveWeather)
        twelveIconView = findViewById(R.id.twelveIcon)
        threeWeather = findViewById(R.id.threeWeather)
        threeIconView = findViewById(R.id.threeIcon)
        sixTimeView = findViewById(R.id.sixTime)
        twelveTimeView = findViewById(R.id.twelveTime)
        threeTimeView = findViewById(R.id.threeTime)
        val request = Request.Builder()
            .url("https://weatherbit-v1-mashape.p.rapidapi.com/current?lon=120.9748491&lat=16.7034897")
            .get()
            .addHeader("X-RapidAPI-Key", "30bf2fdc81msh4e4af22451f12c5p166f00jsnedc31687dcb1")
            .addHeader("X-RapidAPI-Host", "weatherbit-v1-mashape.p.rapidapi.com")
            .build()

        val requestForecast = Request.Builder()
            .url("https://weatherbit-v1-mashape.p.rapidapi.com/forecast/3hourly?lat=16.7&lon=120.97")
            .get()
            .addHeader("X-RapidAPI-Key", "30bf2fdc81msh4e4af22451f12c5p166f00jsnedc31687dcb1")
            .addHeader("X-RapidAPI-Host", "weatherbit-v1-mashape.p.rapidapi.com")
            .build()

        //ito async task
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
//                    println(response.body!!.string())
                    val str_response = response.body!!.string()
                    //creating json object
                    val json_contact: JSONObject = JSONObject(str_response)

//
                    val jsonarray_weather: JSONArray = json_contact.getJSONArray("data")
                    val json_sky: JSONObject =jsonarray_weather.getJSONObject(0)
                    val json_main: JSONObject =json_sky.getJSONObject("weather")
//
                    val size:Int =  json_main.length()
                    println("${json_main} json rsppppooonse")

                    arrayListDetails= ArrayList();
                    val model= Model();
//
                        model.temp= json_sky.getString("temp")
                        model.location= "Tinoc"
                        model.sky= json_main.getString("description")
                        model.skyIcon= json_main.getString("icon")
                        arrayListDetails.add(model)
                        println(arrayListDetails)

                    this@Weather.runOnUiThread(java.lang.Runnable {
                        val addresss = arrayListDetails.get(0).location
                        val skyStats = arrayListDetails.get(0).sky
                        val skyStatsIcon = arrayListDetails.get(0).skyIcon
                        println(skyStatsIcon)
                        temp.text="${arrayListDetails.get(0).temp} °C"
                        place.text= addresss
                        skyStatus.text= skyStats
                        Picasso.get().load( "https://www.weatherbit.io/static/img/icons/$skyStatsIcon.png").into(weatherIcon)
                    })
                }
            }
        }
        )
        client.newCall(requestForecast).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
//                    println(response.body!!.string())
                    val strResponse = response.body!!.string()
                    //creating json object
                    val jsonContact: JSONObject = JSONObject(strResponse)

                    val jsonArrayWeather: JSONArray =jsonContact.getJSONArray("data")
                    val jsonSixSky: JSONObject =jsonArrayWeather.getJSONObject(1)
                    val jsonTwelveSky: JSONObject =jsonArrayWeather.getJSONObject(2)
                    val jsonThreeSky: JSONObject =jsonArrayWeather.getJSONObject(0)
                    val jsonSixSkyWeather: JSONObject =jsonSixSky.getJSONObject("weather")
                    val jsonTwelveSkyWeather: JSONObject =jsonTwelveSky.getJSONObject("weather")
                    val jsonThreeSkyWeather: JSONObject =jsonThreeSky.getJSONObject("weather")


                    arrayListDetails= ArrayList()
                    val model= Model()
                    model.forecastSixDescription= jsonSixSkyWeather.getString("description")
                    model.forecastSixIcon= jsonSixSkyWeather.getString("icon")
                    model.forecastTwelveDescription= jsonTwelveSkyWeather.getString("description")
                    model.forecastTwelveIcon= jsonTwelveSkyWeather.getString("icon")
                    model.forecastThreeDescription= jsonThreeSkyWeather.getString("description")
                    model.forecastThreeIcon= jsonThreeSkyWeather.getString("icon")
                    val sixRainPercent = jsonSixSky.getDouble("pop")
                    val twelveRainPercent = jsonTwelveSky.getDouble("pop")
                    val threeRainPercent = jsonThreeSky.getDouble("pop")
                    model.forecastSixRain = sixRainPercent.toInt()
                    model.forecastTwelveRain = twelveRainPercent.toInt()
                    model.forecastThreeRain = threeRainPercent.toInt()
                    arrayListDetails.add(model)
                    this@Weather.runOnUiThread {
                        val sixDesc = arrayListDetails[0].forecastSixDescription
                        val sixIcon = arrayListDetails[0].forecastSixIcon
                        val sixRain = arrayListDetails[0].forecastSixRain
                        val twelveDesc = arrayListDetails[0].forecastTwelveDescription
                        val twelveIcon = arrayListDetails[0].forecastTwelveIcon
                        val twelveRain = arrayListDetails[0].forecastTwelveRain
                        val threeDesc = arrayListDetails[0].forecastThreeDescription
                        val threeIcon = arrayListDetails[0].forecastThreeIcon
                        val threeRain = arrayListDetails[0].forecastThreeRain

                        val now = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LocalDateTime.now()
                        } else {
                            TODO("VERSION.SDK_INT < O")
                        }
                        val sixTime= now.plusHours(3)
                        val twelveTime= now.plusHours(6)
                        val threeTime= now.plusHours(9)
                        val formatter = DateTimeFormatter.ofPattern("hh:00 a")
                        val sixFormatted = formatter.format(sixTime)
                        val twelveFormatted = formatter.format(twelveTime)
                        val threeFormatted = formatter.format(threeTime)

                        sixWeather.text = sixDesc
                        sixRainView.text = "Rain: ${sixRain.toString()} %"
                        Picasso.get().load("https://www.weatherbit.io/static/img/icons/$sixIcon.png")
                            .into(sixIconView)
                        twelveWeather.text = twelveDesc
                        twelveRainView.text = "Rain: ${twelveRain.toString()} %"
                        Picasso.get().load("https://www.weatherbit.io/static/img/icons/$twelveIcon.png")
                            .into(twelveIconView)
                        threeWeather.text = threeDesc
                        Picasso.get().load("https://www.weatherbit.io/static/img/icons/$threeIcon.png")
                            .into(threeIconView)
                        threeRainView.text = "Rain: ${threeRain.toString()} %"
                        sixTimeView.text = sixFormatted
                        twelveTimeView.text = twelveFormatted
                        threeTimeView.text = threeFormatted
                    }

                }
            }
        })
    }
}