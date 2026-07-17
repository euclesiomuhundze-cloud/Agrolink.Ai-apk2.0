package com.agon.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agon.app.BuildConfig
import com.agon.app.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(
    onBack: () -> Unit,
    viewModel: WeatherViewModel = viewModel()
) {
    val context = LocalContext.current

    val isLoadingWeather by viewModel.isLoadingWeather
    val weatherData by viewModel.weatherData
    val weatherError by viewModel.weatherError
    val isLoadingTip by viewModel.isLoadingTip
    val irrigationTip by viewModel.irrigationTip

    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (granted) {
            viewModel.loadWeather(context, BuildConfig.GEMINI_API_KEY)
        }
    }

    LaunchedEffect(Unit) {
        if (permissionGranted) {
            viewModel.loadWeather(context, BuildConfig.GEMINI_API_KEY)
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Previsão do tempo", style = MaterialTheme.typography.titleLarge)
        }

        when {
            !permissionGranted -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("É necessário permitir o acesso à localização para ver o clima da sua região.")
                }
            }

            isLoadingWeather -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            weatherError != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(weatherError ?: "Erro desconhecido")
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadWeather(context, BuildConfig.GEMINI_API_KEY) }) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }

            weatherData != null -> {
                val data = weatherData!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Agora", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("${data.current?.temperature ?: "--"}°C")
                                Text("Umidade: ${data.current?.humidity ?: "--"}%")
                                Text("Vento: ${data.current?.windSpeed ?: "--"} km/h")
                                Text("Precipitação: ${data.current?.precipitation ?: "--"} mm")
                            }
                        }
                    }

                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("💧 Dica de irrigação", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                if (isLoadingTip) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                } else {
                                    Text(irrigationTip ?: "Sem dica disponível.")
                                }
                            }
                        }
                    }

                    item {
                        Text("Próximos 7 dias", style = MaterialTheme.typography.titleMedium)
                    }

                    val dias = data.daily?.time ?: emptyList()
                    items(dias.size) { i ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(dias[i])
                                Text("${data.daily?.tempMin?.getOrNull(i) ?: "--"}° / ${data.daily?.tempMax?.getOrNull(i) ?: "--"}°")
                                Text("🌧 ${data.daily?.precipitationProbability?.getOrNull(i) ?: "--"}%")
                            }
                        }
                    }
                }
            }
        }
    }
}
