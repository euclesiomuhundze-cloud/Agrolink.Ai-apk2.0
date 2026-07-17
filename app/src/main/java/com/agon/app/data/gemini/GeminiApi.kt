package com.agon.app.data.gemini

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Body
import com.google.gson.annotations.SerializedName

// ==========================================
// 1. CLASSES DE REQUISIÇÃO (REQUEST)
// ==========================================
data class GeminiRequest(
    @SerializedName("contents") val contents: List<Content>,
    @SerializedName("tools") val tools: List<GeminiTool>? = null
)

data class Content(
    @SerializedName("parts") val parts: List<Part>
)

data class Part(
    @SerializedName("text") val text: String? = null,
    @SerializedName("inlineData") val inlineData: InlineData? = null
)

data class InlineData(
    @SerializedName("mimeType") val mimeType: String,
    @SerializedName("data") val data: String
)

// ==========================================
// 2. CLASSES DE RESPOSTA (RESPONSE)
// ==========================================
data class GeminiResponse(
    @SerializedName("candidates") val candidates: List<Candidate>?
)

data class Candidate(
    @SerializedName("content") val content: ContentResponse?
)

data class ContentResponse(
    @SerializedName("parts") val parts: List<PartResponse>?
)

data class PartResponse(
    @SerializedName("text") val text: String?
)

// ==========================================
// 3. INTERFACE DA API (RETROFIT)
// ==========================================
interface AgroLinkApi {
    @POST("v1beta/models/gemini-flash-lite-latest:generateContent")
    suspend fun enviarDados(
        @Query("key") apiKey: String,
        @Body body: GeminiRequest
    ): GeminiResponse
}

// ==========================================
// 4. CLIENTE RETROFIT (SINGLETON)
// ==========================================
object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    val api: AgroLinkApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AgroLinkApi::class.java)
    }
}


data class GeminiTool(
    @SerializedName("google_search") val googleSearch: GoogleSearchTool = GoogleSearchTool()
)

data class GoogleSearchTool(
    val placeholder: String? = null
)
