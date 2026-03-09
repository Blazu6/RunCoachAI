package com.example.runcoachai.data

import retrofit2.http.GET
import retrofit2.http.Query

// To jest nasz "kontrakt" z serwerem Open-Meteo
interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") current: Boolean = true,
        @Query("hourly") hourly: String = "temperature_2m,weathercode" // PROSIMY O DANE GODZINOWE
    ): WeatherResponse
}

data class WeatherResponse(
    val current_weather: CurrentWeather,
    val hourly: HourlyData // DODAJEMY TO
)

data class HourlyData(
    val time: List<String>,            // Lista godzin: ["2023-10-27T10:00", ...]
    val temperature_2m: List<Double>,  // Lista temperatur
    val weathercode: List<Int>         // Lista kodów pogody
)

data class CurrentWeather(
    val temperature: Double,
    val weathercode: Int
)

interface GeocodingApiService {
    @GET("v1/search")
    suspend fun searchCity(
        @Query("name") cityName: String,
        @Query("count") count: Int = 1,
        @Query("language") lang: String = "pl"
    ): GeocodingResponse
}

// Modele dla wyszukiwarki
data class GeocodingResponse(val results: List<CityData>?)
data class CityData(val name: String, val latitude: Double, val longitude: Double)



