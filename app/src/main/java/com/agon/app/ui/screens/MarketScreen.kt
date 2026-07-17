package com.agon.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.agon.app.BuildConfig
import com.agon.app.viewmodel.MarketPrice
import com.agon.app.viewmodel.MarketPricesViewModel
import kotlinx.coroutines.delay

private const val AUTO_REFRESH_INTERVAL_MS = 60 * 60 * 1000L // 1 hora

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    navController: NavHostController,
    viewModel: MarketPricesViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading
    val prices by viewModel.prices
    val error by viewModel.error
    val lastUpdated by viewModel.lastUpdated

    LaunchedEffect(Unit) {
        viewModel.loadPrices(BuildConfig.GEMINI_API_KEY)
        while (true) {
            delay(AUTO_REFRESH_INTERVAL_MS)
            viewModel.loadPrices(BuildConfig.GEMINI_API_KEY)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preços de mercado", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.loadPrices(BuildConfig.GEMINI_API_KEY) },
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Atualizar preços")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            lastUpdated?.let { data ->
                Text(
                    text = "Atualizado: $data",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }

            when {
                isLoading && prices.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null && prices.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(error ?: "Erro desconhecido")
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(onClick = { viewModel.loadPrices(BuildConfig.GEMINI_API_KEY) }) {
                                Text("Tentar novamente")
                            }
                        }
                    }
                }

                prices.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nenhum preço disponível no momento.")
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
                    ) {
                        items(prices) { price ->
                            PriceRow(price)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceRow(price: MarketPrice) {
    val (icon, color) = when (price.tendencia) {
        "subida" -> Icons.Default.ArrowUpward to Color(0xFF2E7D32)
        "descida" -> Icons.Default.ArrowDownward to Color(0xFFC62828)
        else -> Icons.Default.Remove to Color(0xFF9E9E9E)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp),
        shape = RoundedCornerShape(14.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(price.produto, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${price.precoMt} MT",
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    "por ${price.unidade}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.width(14.dp))
                    Text(
                        price.tendencia,
                        color = color,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
