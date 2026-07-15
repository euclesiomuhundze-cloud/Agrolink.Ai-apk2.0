package com.agon.app.ui.screens

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.agon.app.data.SkillCategory
import com.agon.app.data.WorkerProfile
import com.agon.app.ui.components.InitialsAvatar
import com.agon.app.ui.components.RatingStars
import com.agon.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkersListScreen(navController: NavHostController, viewModel: AppViewModel) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<SkillCategory?>(null) }

    val filtered = viewModel.workers.filter { worker ->
        (selectedCategory == null || worker.skill == selectedCategory) &&
            (query.isBlank() || worker.name.contains(query, ignoreCase = true) || worker.location.contains(query, ignoreCase = true))
    }.sortedBy { it.distanceKm }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profissionais perto de ti", fontWeight = FontWeight.Bold) },
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
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                placeholder = { Text("Pesquisar por nome ou localização") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Todos") },
                    )
                }
                items(SkillCategory.entries) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = if (selectedCategory == category) null else category },
                        label = { Text(category.displayName) },
                        leadingIcon = {
                            Icon(category.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (viewModel.isLoadingWorkers.value) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (viewModel.workersError.value != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Não foi possível carregar os profissionais. Verifica a tua ligação.",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            } else if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Ainda não há profissionais registados com estes filtros.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    items(filtered) { worker ->
                        WorkerCard(
                            worker = worker,
                            isSaved = viewModel.isWorkerSaved(worker.id),
                            onSaveToggle = { viewModel.toggleSavedWorker(worker.id) },
                            onClick = { navController.navigate("biscato/worker/${worker.id}") },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkerCard(
    worker: WorkerProfile,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            InitialsAvatar(name = worker.name, size = 52.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(worker.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    if (worker.verified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.VerifiedUser,
                            contentDescription = "Verificado",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
                Text(
                    worker.skill.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = worker.skill.color,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingStars(rating = worker.rating, starSize = 13.dp)
                    Text(
                        " ${worker.rating} (${worker.reviewCount})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp),
                    )
                    Text(
                        "${worker.location} · ${worker.distanceKm} km",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onSaveToggle) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Guardar",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
