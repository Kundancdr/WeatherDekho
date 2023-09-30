package com.example.weatherlive

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.weatherlive.R.drawable.haze
import com.example.weatherlive.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val OPEN_WEATHER_MAP_BASE_URL = "https://api.openweathermap.org/data/2.5/"
const val OPEN_WEATHER_MAP_API_KEY = "c445bb800d48052e30278d1efd7085f5"


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchInput:EditText
    private lateinit var searchIcon:ImageView
    private lateinit var weatherImage: ImageView
    private lateinit var temperatureText:TextView
    private lateinit var locationText:TextView
    private lateinit var weatherConditionTextView: TextView
    private lateinit var city:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView( binding.root)

        searchInput=binding.SearchInput
        searchIcon=binding.searchIcon
        city="jabalpur"

        searchIcon.setOnClickListener{
            city=searchInput.text.toString()
            println(city)

            hideKeyboard()
            fetchWeatherData()
        }
       fetchWeatherData()


        }

    private fun fetchWeatherData() {
        weatherImage=binding.WeatherImage
        temperatureText=binding.TemperatureText
        locationText=binding.locationText
         weatherConditionTextView=binding.WeatherConditionText

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(OPEN_WEATHER_MAP_BASE_URL)
            .build()
            .create(ApiInterface::class.java)


        val response = retrofit.getWeatherData(city, OPEN_WEATHER_MAP_API_KEY)
        response.enqueue(/* callback = */ object :Callback<WeatherCity> {
            override fun onResponse(call: Call<WeatherCity>, response: Response<WeatherCity>) {
                val responseBody = response.body()

                if (response.isSuccessful && responseBody != null) {
                    val KelvinTemperature = responseBody.main.temp
                    val celsiusTemperature = KelvinTemperature-273.15
                    val weathertype = responseBody.weather[0].main
                    val city = responseBody.name

                    temperatureText.text = "${celsiusTemperature.toInt()}°C"
                    locationText.text = city
                    weatherConditionTextView.text = weathertype
                    setWeatherImage(weathertype)
                }

            }

            override fun onFailure(call: Call<WeatherCity>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun setWeatherImage(weathertype:String){
        val drawableResId = when(weathertype.toLowerCase()){
            "sunny" -> R.drawable.sun
            "clouds" -> R.drawable.clouds
            "rain" -> R.drawable.raining
            "thunder"-> R.drawable.thunderstorm
            "haze" -> R.drawable.haze

            else ->R.drawable.mausam
        }
        weatherImage.setImageResource(drawableResId)

    }

    private fun hideKeyboard(){
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(searchInput.windowToken,0)
    }

}
