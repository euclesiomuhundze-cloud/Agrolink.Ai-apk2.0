import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView
    private val apiKey = "SUA_API_KEY_AQUI" // Coloque sua chave aqui

    // Lançador da câmera
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            resultTextView.text = "Analisando..."
            // Salva o bitmap em um arquivo temporário para enviar ao serviço
            val file = File(cacheDir, "temp_image.jpg")
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
            fos.close()

            // Chama a lógica de análise
            lifecycleScope.launch {
                val resultado = AgroLinkService.analisarPlanta(apiKey, file)
                resultTextView.text = resultado ?: "Nenhum resultado retornado."
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Certifique-se de ter um layout simples com um botão (btnCapturar) e um texto (txtResultado)
        setContentView(R.layout.activity_main)

        val btnCapturar = findViewById<Button>(R.id.btnCapturar)
        resultTextView = findViewById(R.id.txtResultado)

        btnCapturar.setOnClickListener {
            takePictureLauncher.launch(null)
        }
    }
}

