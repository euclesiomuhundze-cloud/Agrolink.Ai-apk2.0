package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.agon.app.data.PriceTrend
import com.agon.app.data.SampleData
import com.agon.app.ui.components.SectionHeader
import com.agon.app.viewmodel.AppViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AgroHubScreen(navController: NavHostController, viewModel: AppViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AgroIA", fontWeight = FontWeight.Bold)
                        Text(
                            "Mais produtividade no campo",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                SectionHeader(
                    title = "Ferramentas essenciais",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AgroToolCard(
                        icon = Icons.Outlined.PhotoCamera,
                        title = "Diagnóstico de doenças",
                        description = "Tira uma foto da folha e recebe recomendações imediatas de tratamento.",
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { navController.navigate("agro/diagnosis") },
                    )
                    AgroToolCard(
                        icon = Icons.Default.WbSunny,
                        title = "Previsão do tempo",
                        description = "7 dias de previsão com dicas de irrigação e proteção de culturas.",
                        color = Color(0xFF3E8EDE),
                        onClick = { navController.navigate("agro/weather") },
                    )
                    AgroToolCard(
                        icon = Icons.Outlined.ShowChart,
                        title = "Preços de mercado",
                        description = "Consulta os preços atualizados dos principais produtos agrícolas.",
                        color = MaterialTheme.colorScheme.secondary,
                        onClick = { navController.navigate("agro/market") },
                    )
                    AgroToolCard(
                        icon = Icons.Outlined.Lightbulb,
                        title = "Dicas agrícolas",
                        description = "Boas práticas em português e línguas locais para melhorar a colheita.",
                        color = Color(0xFF7A5230),
                        onClick = { navController.navigate("agro/tips") },
                    )
                    AgroToolCard(
                        icon = Icons.Default.Storefront,
                        title = "Ligação a compradores",
                        description = "Publica a tua produção e liga-te diretamente a compradores da região.",
                        color = Color(0xFF2E7D32),
                        onClick = { navController.navigate("agro/marketplace") },
                    )
                }
            }
            item {
                SectionHeader(
                    title = "Preços em destaque",
                    subtitle = "Atualizado hoje",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(SampleData.marketPrices.take(5)) { price ->
                        MiniPriceCard(price.cropName, price.price, price.trend)
                    }
                }
            }
            item {
                if (viewModel.diagnosisHistory.isNotEmpty()) {
                    SectionHeader(
                        title = "Último diagnóstico",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    )
                    val record = viewModel.diagnosisHistory.first()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(16.dp),
                        onClick = { navController.navigate("agro/diagnosis_result") },
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoCamera,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.padding(start = 10.dp))
                            Column {
                                Text(record.disease.name, fontWeight = FontWeight.Bold)
                                Text(
                                    record.disease.cropType,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AgroToolCard(
    icon: ImageVector,
    title: String,
    description: String,
    color: Color,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.padding(start = 14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun MiniPriceCard(name: String, price: Int, trend: PriceTrend) {
    val trendColor = when (trend) {
        PriceTrend.SUBIU -> Color(0xFF2E7D32)
        PriceTrend.DESCEU -> Color(0xFFC62828)
        PriceTrend.ESTAVEL -> Color(0xFF9E9E9E)
    }
    Card(
        modifier = Modifier.height(90.dp),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Column {
                Text("$price Kz", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    when (trend) {
                        PriceTrend.SUBIU -> "▲ subida"
                        PriceTrend.DESCEU -> "▼ descida"
                        PriceTrend.ESTAVEL -> "● estável"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = trendColor,
                )
            }
        }
    }
}
