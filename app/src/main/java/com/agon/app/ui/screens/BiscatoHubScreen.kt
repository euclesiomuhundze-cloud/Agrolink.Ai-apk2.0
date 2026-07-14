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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.agon.app.data.JobStatus
import com.agon.app.data.SampleData
import com.agon.app.ui.components.SectionHeader
import com.agon.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiscatoHubScreen(navController: NavHostController, viewModel: AppViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("BiscatoHub", fontWeight = FontWeight.Bold)
                        Text(
                            "Oportunidades de trabalho perto de ti",
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatBox(
                        modifier = Modifier.weight(1f),
                        value = "${SampleData.jobs.count { it.status == JobStatus.ABERTO }}",
                        label = "Biscatos abertos",
                    )
                    StatBox(
                        modifier = Modifier.weight(1f),
                        value = "${SampleData.workers.size}",
                        label = "Profissionais",
                    )
                }
            }
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    BiscatoToolCard(
                        icon = Icons.Default.Work,
                        title = "Ver biscatos disponíveis",
                        description = "Encontra trabalhos publicados por clientes perto de ti.",
                        color = MaterialTheme.colorScheme.secondary,
                        onClick = { navController.navigate("biscato/jobs") },
                    )
                    BiscatoToolCard(
                        icon = Icons.Default.PersonSearch,
                        title = "Procurar profissionais",
                        description = "Pesquisa por habilidade e localização: pedreiro, eletricista e mais.",
                        color = Color(0xFF3E8EDE),
                        onClick = { navController.navigate("biscato/workers") },
                    )
                    BiscatoToolCard(
                        icon = Icons.Default.PostAdd,
                        title = "Publicar um biscato",
                        description = "Tens um serviço para fazer? Publica e recebe propostas rapidamente.",
                        color = Color(0xFF2E7D32),
                        onClick = { navController.navigate("biscato/post_job") },
                    )
                    BiscatoToolCard(
                        icon = Icons.Default.Groups,
                        title = "Criar o meu perfil",
                        description = "Mostra as tuas habilidades e começa a receber pedidos de clientes.",
                        color = Color(0xFF7A5230),
                        onClick = { navController.navigate("biscato/create_profile") },
                    )
                }
            }
            item {
                SectionHeader(
                    title = "Biscatos urgentes",
                    subtitle = "Precisam de ajuda hoje",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(SampleData.jobs.filter { it.urgent }) { job ->
                        Card(
                            modifier = Modifier.width(220.dp),
                            shape = RoundedCornerShape(16.dp),
                            onClick = { navController.navigate("biscato/job/${job.id}") },
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Icon(
                                    imageVector = job.category.icon,
                                    contentDescription = null,
                                    tint = job.category.color,
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    job.title,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    job.budgetHint,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.SemiBold,
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
private fun StatBox(modifier: Modifier = Modifier, value: String, label: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun BiscatoToolCard(
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
