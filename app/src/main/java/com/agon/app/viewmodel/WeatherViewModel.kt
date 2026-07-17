package com.agon.app.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agon.app.data.weather.WeatherResponse
import com.agon.app.data.weather.WeatherRetrofitClient
import com.agon.app.data.gemini.Content
import com.agon.app.data.gemini.GeminiRequest
import com.agon.app.data.gemini.Part
import com.agon.app.data.gemini.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WeatherViewModel : ViewModel() {

    val isLoadingWeather = mutableStateOf(false)
    val weatherData = mutableStateOf<WeatherResponse?>(null)
    val weatherError = mutableStateOf<String?>(null)

    val isLoadingTip = mutableStateOf(false)
    val irrigationTip = mutableStateOf<String?>(null)

    @SuppressLint("MissingPermission")
    fun loadWeather(context: Context, apiKey: String) {
        viewModelScope.launch {
            isLoadingWeather.value = true
            weatherError.value = null
            irrigationTip.value = null
            try {
                val fusedClient: FusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(context)

                val location = fusedClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    null
                ).await()

                if (location == null) {
                    weatherError.value = "Não foi possível obter a localização. Verifique se o GPS está ativado."
                    isLoadingWeather.value = false
                    return@launch
                }

                val response = WeatherRetrofitClient.api.getForecast(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
                weatherData.value = response
                isLoadingWeather.value = false

                gerarDicaIrrigacao(response, apiKey)
            } catch (e: Exception) {
                weatherError.value = "Erro ao obter o clima: ${e.message}"
                isLoadingWeather.value = false
            }
        }
    }

    private fun gerarDicaIrrigacao(weather: WeatherResponse, apiKey: String) {
        viewModelScope.launch {
            isLoadingTip.value = true
            try {
                val resumo = buildString {
                    append("Temperatura atual: ${weather.current?.temperature}°C. ")
                    append("Umidade: ${weather.current?.humidity}%. ")
                    append("Vento: ${weather.current?.windSpeed} km/h. ")
                    append("Previsão dos próximos dias — ")
                    weather.daily?.time?.forEachIndexed { i, dia ->
                        val max = weather.daily.tempMax?.getOrNull(i)
                        val min = weather.daily.tempMin?.getOrNull(i)
                        val chuva = weather.daily.precipitationProbability?.getOrNull(i)
                        append("$dia: máx $max°C, mín $min°C, chance de chuva $chuva%; ")
                    }
                }

                val prompt = "Com base nestes dados meteorológicos para uma propriedade agrícola, " +
                        "dê uma dica curta e prática (máximo 3 frases) sobre irrigação e proteção " +
                        "das culturas para os próximos dias. Dados: $resumo"

                val request = GeminiRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = prompt)))
                    )
                )

                val response = RetrofitClient.api.enviarDados(apiKey, request)
                val texto = response.candidates?.firstOrNull()
                    ?.content?.parts?.firstOrNull()?.text

                irrigationTip.value = texto ?: "Não foi possível gerar a dica no momento."
            } catch (e: Exception) {
                irrigationTip.value = "Não foi possível gerar a dica de irrigação: ${e.message}"
            } finally {
                isLoadingTip.value = false
            }
        }
    }
}
