package com.agon.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Carpenter
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Woman
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class SkillCategory(val displayName: String, val icon: ImageVector, val color: Color) {
    PEDREIRO("Pedreiro", Icons.Default.Build, Color(0xFF8D6E63)),
    CARPINTEIRO("Carpinteiro", Icons.Default.Carpenter, Color(0xFF6D4C41)),
    CANALIZADOR("Canalizador", Icons.Default.Plumbing, Color(0xFF3E8EDE)),
    ELETRICISTA("Eletricista", Icons.Default.ElectricalServices, Color(0xFFF9A825)),
    COSTUREIRA("Costureira", Icons.Default.Woman, Color(0xFFD81B60)),
    EXPLICADOR("Explicador", Icons.Default.MenuBook, Color(0xFF8E24AA)),
    JARDINAGEM("Jardinagem", Icons.Default.Grass, Color(0xFF2E7D32)),
    LIMPEZA("Limpeza", Icons.Default.CleaningServices, Color(0xFF00897B)),
    OUTRO("Outro", Icons.Default.Build, Color(0xFF757575)),
}

data class WorkerProfile(
    val id: String,
    val name: String,
    val skill: SkillCategory,
    val bio: String,
    val location: String,
    val distanceKm: Double,
    val rating: Double,
    val reviewCount: Int,
    val completedJobs: Int,
    val priceHint: String,
    val verified: Boolean,
    val reviews: List<Review> = emptyList(),
)

data class Review(
    val id: String,
    val reviewerName: String,
    val rating: Double,
    val comment: String,
    val date: String,
)

enum class JobStatus {
    ABERTO,
    EM_ANDAMENTO,
    CONCLUIDO,
    CANCELADO,
}

data class JobPost(
    val id: String,
    val title: String,
    val category: SkillCategory,
    val description: String,
    val location: String,
    val distanceKm: Double,
    val budgetHint: String,
    val postedBy: String,
    val postedAgo: String,
    val urgent: Boolean,
    val status: JobStatus,
)

data class ChatMessage(
    val id: String,
    val senderIsMe: Boolean,
    val text: String,
    val timeLabel: String,
)
