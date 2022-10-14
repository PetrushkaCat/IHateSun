package com.example.ihatesun.activity

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ihatesun.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    companion object {
        lateinit var coordinates: Map<String, Double>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerLauncher()
        checkPermissions()

        val textViewUV = findViewById<TextView>(R.id.textUV)
        val textViewWeather = findViewById<TextView>(R.id.textWeather)
        val buttonUpdate = findViewById<Button>(R.id.buttonUpdateUV)

        mainViewModel.uv.observe(this) {
            val uv = it?.result?.uv ?: -1.0

            if (uv in 0.0..2.0) {
                textViewUV.text = "UV = ${it?.result?.uv}\nYou don't have to take a hat."
            } else if (2.0 < uv && uv <= 7.0) {
                textViewUV.text = "UV = ${it?.result?.uv}\nYou should probably take a hat."
            } else if (uv > 7.0) {
                textViewUV.text = "UV = ${it?.result?.uv}\nDon't go out!"
            } else {
                textViewUV.text = getString(R.string.dataLoadingError)
            }
        }

        mainViewModel.weather.observe(this) {
            val temperature = it?.main?.temp ?: -1
            val weatherMain = it?.weather?.get(0)?.main
            val weatherDescription = it?.weather?.get(0)?.description

            if(temperature != -1 ) {

                textViewWeather.text = "Temperature = $temperature°C\n$weatherDescription\n"

                when (weatherMain) {
                    "Drizzle" -> textViewWeather.append("Take an umbrella")
                    "Rain" -> textViewWeather.append("Take an umbrella")
                    "Thunderstorm" -> textViewWeather.append("Don't go out!")
                    "Snow" -> textViewWeather.append("Nothing can save you from the snow...")
                    "Clear" -> textViewWeather.append("...")
                    "Clouds" -> textViewWeather.append("Clouds... perfect!")

                }
            } else {
                textViewWeather.text = getString(R.string.dataLoadingError)
            }
        }

        buttonUpdate.setOnClickListener {
            //Tasks require coroutine
            CoroutineScope(Dispatchers.IO).launch {
                coordinates = getCoordinates()
                mainViewModel.update()
            }
        }
    }

    private fun checkPermissions() {
        if ((
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {

            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION))
        }

    }

    private fun registerLauncher() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if(it[Manifest.permission.ACCESS_COARSE_LOCATION] != true &&
                it[Manifest.permission.ACCESS_FINE_LOCATION] != true) {

                finishAndRemoveTask()
            }
        }
    }

    private fun getCoordinates(): Map<String, Double> {
        val coordinates = hashMapOf<String, Double>()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return emptyMap()
        }

        val task = LocationServices.getFusedLocationProviderClient(this).lastLocation
        Tasks.await(task)
        coordinates["lat"] = task.result.latitude
        coordinates["lng"] = task.result.longitude
        return coordinates
    }

}