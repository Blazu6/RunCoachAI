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
        // ZMIANA: dopisujemy precipitation_probability po przecinku
        @Query("hourly") hourly: String = "temperature_2m,weathercode,precipitation_probability"
    ): WeatherResponse
}

// TO ZOSTAJE TAK JAK MIAŁEŚ:
data class WeatherResponse(
    val current_weather: CurrentWeather,
    val hourly: HourlyData
)

// ZMIANA: dodajemy nową listę na końcu
data class HourlyData(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val weathercode: List<Int>,
    val precipitation_probability: List<Int> // NOWOŚĆ: szansa na deszcz w %
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



