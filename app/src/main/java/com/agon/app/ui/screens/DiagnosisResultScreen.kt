package com.agon.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.agon.app.data.Severity
import com.agon.app.ui.components.EmptyState
import com.agon.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisResultScreen(navController: NavHostController, viewModel: AppViewModel) {
    val record = viewModel.lastDiagnosis.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultado do diagnóstico", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { padding ->
        if (record == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = { Icon(Icons.Default.Science, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    title = "Nenhum diagnóstico ainda",
                    message = "Volta e escolhe uma foto para analisar.",
                )
            }
            return@Scaffold
        }

        val disease = record.disease
        val (severityColor, severityLabel) = when (disease.severity) {
            Severity.SAUDAVEL -> Color(0xFF2E7D32) to "Saudável"
            Severity.BAIXA -> Color(0xFF66BB6A) to "Severidade baixa"
            Severity.MEDIA -> Color(0xFFEF6C00) to "Severidade média"
            Severity.ALTA -> Color(0xFFC62828) to "Severidade alta"
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 10f),
                ) {
                    Image(
                        painter = painterResource(record.leaf.imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (disease.severity == Severity.SAUDAVEL) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = severityColor,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(severityLabel, color = severityColor, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(disease.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    Text(
                        "Cultura: ${disease.cropType}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            item {
                InfoCard(
                    icon = Icons.Default.Science,
                    title = "Sintomas identificados",
                    text = disease.symptoms,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            item {
                InfoCard(
                    icon = Icons.Default.Shield,
                    title = "Tratamento recomendado",
                    text = disease.treatment,
                    color = Color(0xFF3E8EDE),
                )
            }
            item {
                InfoCard(
                    icon = Icons.Default.CheckCircle,
                    title = "Prevenção futura",
                    text = disease.prevention,
                    color = Color(0xFF2E7D32),
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate("agro/diagnosis") { popUpTo("agro/diagnosis") { inclusive = true } } },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Nova análise")
                    }
                    Button(
                        onClick = { navController.navigate("agro/marketplace") },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Vender produção")
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    text: String,
    color: Color,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
