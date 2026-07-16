package com.agon.app.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.compose.rememberAsyncImagePainter
import androidx.navigation.NavHostController
import com.agon.app.BuildConfig
import com.agon.app.ui.components.SectionHeader
import com.agon.app.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

private fun createImageFile(context: Context): File {
    val dir = File(context.cacheDir, "diagnosis_images").apply { mkdirs() }
    return File(dir, "capture_${System.currentTimeMillis()}.jpg")
}

private fun uriToFile(context: Context, uri: Uri, destination: File) {
    context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(destination).use { output ->
            input.copyTo(output)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropDiagnosisScreen(navController: NavHostController, viewModel: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var pendingCameraFile by remember { mutableStateOf<File?>(null) }
    var previewFile by remember { mutableStateOf<File?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success && pendingCameraFile != null) {
            previewFile = pendingCameraFile
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val destination = createImageFile(context)
                uriToFile(context, uri, destination)
                previewFile = destination
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diagnóstico de doenças", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            SectionHeader(
                title = "Tira ou escolhe uma foto",
                subtitle = "A IA vai analisar a folha e identificar possíveis pragas ou doenças",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (previewFile != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = previewFile),
                        contentDescription = "Foto selecionada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .padding(1.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "Nenhuma foto selecionada",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                if (viewModel.isAiDiagnosing.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.height(22.dp), strokeWidth = 2.dp)
                            Text(
                                "A analisar imagem com IA...",
                                modifier = Modifier.padding(start = 12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = {
                                val file = createImageFile(context)
                                pendingCameraFile = file
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file,
                                )
                                cameraLauncher.launch(uri)
                            },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Text("  Câmera")
                        }
                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            Text("  Galeria")
                        }
                    }

                    if (viewModel.aiDiagnosisError.value != null) {
                        Text(
                            viewModel.aiDiagnosisError.value.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }

                    Button(
                        onClick = {
                            val file = previewFile ?: return@Button
                            viewModel.runAiDiagnosis(file, BuildConfig.GEMINI_API_KEY) {
                                navController.navigate("agro/ai_diagnosis_result")
                            }
                        },
                        enabled = previewFile != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(top = 12.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(
                            "Analisar planta",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}
