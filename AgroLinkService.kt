import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AgroLinkService {

    // 1. Converte um arquivo de imagem (File) para uma String Base64
    fun converterImagemParaBase64(imageFile: File): String {
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        val outputStream = ByteArrayOutputStream()
        // Comprime a imagem para JPEG para não estourar o limite de tamanho da requisição
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val imageBytes = outputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP)
    }

    // 2. Envia a imagem convertida para o Gemini analisar
    suspend fun analisarPlanta(apiKey: String, imageFile: File): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Converte a imagem tirada pelo telemóvel para Base64
                val imagemBase64 = converterImagemParaBase64(imageFile)

                // Monta o prompt para o Gemini identificar o problema na planta
                val promptText = "Analise esta imagem de folha/planta. Identifique se existe alguma praga ou doença. Se sim, dê o nome da doença e sugira soluções práticas de tratamento."

                // Cria o corpo da requisição exatamente na estrutura que o seu agrolink.kt espera
                val request = GeminiRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(
                                Part(text = promptText),
                                Part(
                                    inlineData = InlineData(
                                        mimeType = "image/jpeg",
                                        data = imagemBase64
                                    )
                                )
                            )
                        )
                    )
                )

                // Executa a chamada usando o RetrofitClient que configuramos
                val response = RetrofitClient.api.enviarDados(apiKey, request)

                // Retorna o texto explicativo da resposta do Gemini
                response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

            } catch (e: Exception) {
                e.printStackTrace()
                "Erro ao analisar imagem: ${e.localizedMessage}"
            }
        }
    }
}

