package com.example.runcoachai.ui
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runcoachai.data.GeocodingApiService
import com.example.runcoachai.data.HourlyData
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherViewModel : ViewModel() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val geocodingApi = Retrofit.Builder()
        .baseUrl("https://geocoding-api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeocodingApiService::class.java)

    private val apiService = retrofit.create(com.example.runcoachai.data.WeatherApiService::class.java)

    var temperature by mutableStateOf("--")
        private set
    var weatherIcon by mutableStateOf("☀️")
        private set
    var weatherDescription by mutableStateOf("Pobieranie...")
        private set

    var cityInput by mutableStateOf("") // To co wpisujesz w pole

    var currentCityName by mutableStateOf("Poznań")
        private set

    var hourlyForecast by mutableStateOf<HourlyData?>(null)
        private set

    var aiRecommendation by mutableStateOf("Czekam na dane o pogodzie...")
        private set

    private fun updateWeatherDetails(code: Int) {
        val (icon, desc) = when (code) {
            0 -> "☀️" to "Czyste niebo"
            1, 2, 3 -> "⛅" to "Częściowe zachmurzenie"
            45, 48 -> "🌫️" to "Mgła"
            51, 53, 55 -> "🌧️" to "Mżawka"
            61, 63, 65 -> "🌧️" to "Deszcz"
            71, 73, 75 -> "❄️" to "Śnieg"
            80, 81, 82 -> "🌦️" to "Przemieszczające się opady"
            95, 96, 99 -> "⛈️" to "Burza"
            else -> "❓" to "Nieznana pogoda"
        }
        weatherIcon = icon
        weatherDescription = desc
    }

    fun fetchWeather(lat: Double, lon: Double, cityName: String) {
        viewModelScope.launch {
            try {
                temperature = "--"
                val response = apiService.getCurrentWeather(lat, lon)
                println("ODPOWIEDŹ Z SERWERA: $response")
                temperature = "${response.current_weather.temperature}°C"
                updateWeatherDetails(response.current_weather.weathercode)
                currentCityName = cityName
                hourlyForecast = response.hourly
                analyzeWeatherForRunning(response.hourly)
            } catch (e: Exception) {
                android.util.Log.e("POGODA", "Błąd pobierania", e)
                temperature = "Błąd"
            }
        }
    }

    fun searchAndFetchWeather() {
        viewModelScope.launch {
            try {
                val geoResponse = geocodingApi.searchCity(cityInput)
                val city = geoResponse.results?.firstOrNull()

                if (city != null) {
                    val weatherResponse = apiService.getCurrentWeather(city.latitude, city.longitude)
                    temperature = "${weatherResponse.current_weather.temperature}°C"
                    updateWeatherDetails(weatherResponse.current_weather.weathercode)
                    currentCityName = city.name
                    hourlyForecast = weatherResponse.hourly
                    analyzeWeatherForRunning(weatherResponse.hourly)
                    cityInput = ""
                } else {
                    temperature = "Nie znaleziono"
                }
            } catch (e: Exception) {
                temperature = "Błąd"
            }
        }
    }

    fun getWeatherIcon(code: Int): String {
        return when (code) {
            0 -> "☀️" // Czyste niebo
            1, 2, 3 -> "⛅" // Częściowe zachmurzenie
            45, 48 -> "🌫️" // Mgła
            51, 53, 55, 56, 57 -> "🌧️" // Mżawka i marznąca mżawka (dodane 56, 57)
            61, 63, 65, 66, 67 -> "🌧️" // Deszcz i marznący deszcz (dodane 66, 67)
            71, 73, 75, 77 -> "❄️" // Śnieg i ziarna śniegu (dodane 77)
            80, 81, 82 -> "🌦️" // Przelotny deszcz
            85, 86 -> "❄️" // Przelotny śnieg (dodane 85, 86)
            95, 96, 99 -> "⛈️" // Burza z piorunami / grad
            else -> "❓" // Rezerwa na totalne anomalie
        }
    }

    private fun analyzeWeatherForRunning(hourlyData: com.example.runcoachai.data.HourlyData) {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH", java.util.Locale.getDefault())
        val currentFullHour = sdf.format(java.util.Date())
        val startIndex = hourlyData.time.indexOfFirst { it.startsWith(currentFullHour) }.coerceAtLeast(0)

        var bestHour = ""
        var bestTemp = -100.0
        var foundIdeal = false

        // Sprawdzamy najbliższe 8 godzin
        for (i in 0 until 8) {
            val actualIndex = startIndex + i
            if (actualIndex >= hourlyData.time.size) break

            val temp = hourlyData.temperature_2m[actualIndex]
            val rainProb = hourlyData.precipitation_probability?.get(actualIndex) ?: 0
            val code = hourlyData.weathercode[actualIndex]

            // Warunki idealne: brak deszczu (szansa < 20%), temperatura między 10 a 20 stopni
            if (rainProb < 20 && code < 50 && temp in 10.0..20.0) {
                bestHour = hourlyData.time[actualIndex].substringAfter("T")
                bestTemp = temp
                foundIdeal = true
                break // Znaleźliśmy idealną godzinę, przerywamy szukanie!
            }
        }

        aiRecommendation = if (foundIdeal) {
            "Trener AI radzi: Najlepsze warunki na bieg będą o $bestHour ($bestTemp°C). Zakładaj buty!"
        } else {
            "Trener AI radzi: Warunki nie są dziś idealne. Rozważ krótszy bieg lub ubierz się odpowiednio do pogody."
        }
    }


}