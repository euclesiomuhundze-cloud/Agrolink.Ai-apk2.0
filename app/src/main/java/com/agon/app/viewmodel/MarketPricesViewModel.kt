package com.agon.app.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agon.app.data.gemini.Content
import com.agon.app.data.gemini.GeminiRequest
import com.agon.app.data.gemini.GeminiTool
import com.agon.app.data.gemini.Part
import com.agon.app.data.gemini.RetrofitClient
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch

data class MarketPrice(
    @SerializedName("produto") val produto: String,
    @SerializedName("precoMt") val precoMt: Double,
    @SerializedName("unidade") val unidade: String,
    @SerializedName("tendencia") val tendencia: String // "subida", "descida" ou "estavel"
)

class MarketPricesViewModel : ViewModel() {

    val isLoading = mutableStateOf(false)
    val prices = mutableStateOf<List<MarketPrice>>(emptyList())
    val error = mutableStateOf<String?>(null)
    val lastUpdated = mutableStateOf<String?>(null)

    fun loadPrices(apiKey: String) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val prompt = """
                    Pesquise no site agricultura.gov.mz/sima/ o boletim "Quente-Quente" mais recente 
                    com os preços de mercado agrícola de Moçambique. Preciso dos preços a nível 
                    retalhista (Mt/Kg) dos seguintes produtos, se disponíveis: milho, feijão (manteiga 
                    ou nhemba), mandioca, batata-doce, arroz, amendoim.

                    Responda APENAS com um JSON válido, sem texto antes ou depois, sem markdown, 
                    no seguinte formato exato:
                    {
                      "dataAtualizacao": "texto indicando a semana/data do boletim",
                      "produtos": [
                        {"produto": "Milho", "precoMt": 12500, "unidade": "Mt/Kg", "tendencia": "subida"},
                        {"produto": "Feijão", "precoMt": 21000, "unidade": "Mt/Kg", "tendencia": "estavel"}
                      ]
                    }
                    O campo tendencia deve ser exatamente "subida", "descida" ou "estavel".
                    Se não encontrar dados para algum produto, omita-o da lista.
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = prompt)))
                    ),
                    tools = listOf(GeminiTool())
                )

                val response = RetrofitClient.api.enviarDados(apiKey, request)
                val texto = response.candidates?.firstOrNull()
                    ?.content?.parts?.firstOrNull()?.text
                    ?: throw Exception("Resposta vazia da IA")

                val jsonLimpo = extrairJson(texto)
                val resultado = Gson().fromJson(jsonLimpo, MarketPricesResult::class.java)

                prices.value = resultado.produtos
                lastUpdated.value = resultado.dataAtualizacao
                isLoading.value = false
            } catch (e: Exception) {
                error.value = "Não foi possível carregar os preços de mercado: ${e.message}"
                isLoading.value = false
            }
        }
    }

    private fun extrairJson(texto: String): String {
        val inicio = texto.indexOf('{')
        val fim = texto.lastIndexOf('}')
        return if (inicio != -1 && fim != -1 && fim > inicio) {
            texto.substring(inicio, fim + 1)
        } else {
            texto
        }
    }
}

data class MarketPricesResult(
    @SerializedName("dataAtualizacao") val dataAtualizacao: String,
    @SerializedName("produtos") val produtos: List<MarketPrice>
)
