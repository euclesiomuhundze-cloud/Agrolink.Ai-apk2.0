package com.agon.app.data.weather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName

// ==========================================
// 1. CLASSE DE RESPOSTA (RESPONSE)
// ==========================================
data class WeatherResponse(
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("current") val current: CurrentWeather?,
    @SerializedName("daily") val daily: DailyWeather?
)

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double?,
    @SerializedName("relative_humidity_2m") val humidity: Int?,
    @SerializedName("wind_speed_10m") val windSpeed: Double?,
    @SerializedName("precipitation") val precipitation: Double?,
    @SerializedName("weather_code") val weatherCode: Int?
)

data class DailyWeather(
    @SerializedName("time") val time: List<String>?,
    @SerializedName("temperature_2m_max") val tempMax: List<Double>?,
    @SerializedName("temperature_2m_min") val tempMin: List<Double>?,
    @SerializedName("precipitation_sum") val precipitationSum: List<Double>?,
    @SerializedName("precipitation_probability_max") val precipitationProbability: List<Int>?,
    @SerializedName("wind_speed_10m_max") val windSpeedMax: List<Double>?,
    @SerializedName("weather_code") val weatherCode: List<Int>?
)

// ==========================================
// 2. INTERFACE DA API (RETROFIT)
// ==========================================
interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,wind_speed_10m,precipitation,weather_code",
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,precipitation_sum,precipitation_probability_max,wind_speed_10m_max,weather_code",
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}

// ==========================================
// 3. CLIENTE RETROFIT (SINGLETON)
// ==========================================
object WeatherRetrofitClient {
    private const val BASE_URL = "https://api.open-meteo.com/"

    val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}
