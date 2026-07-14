package com.agon.app.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Carpenter
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Yard

enum class SkillCategory(val displayName: String, val icon: ImageVector, val color: Color) {
    PEDREIRO("Pedreiro", Icons.Default.Construction, Color(0xFF8D6E63)),
    CARPINTEIRO("Carpinteiro", Icons.Default.Carpenter, Color(0xFF6D4C41)),
    CANALIZADOR("Canalizador", Icons.Default.Plumbing, Color(0xFF3E8EDE)),
    ELETRICISTA("Eletricista", Icons.Default.ElectricBolt, Color(0xFFF9A825)),
    EXPLICADOR("Explicador", Icons.Default.School, Color(0xFF5E35B1)),
    COSTUREIRA("Costureira", Icons.Default.Checkroom, Color(0xFFD81B60)),
    LIMPEZA("Limpeza", Icons.Default.CleaningServices, Color(0xFF00897B)),
    TRANSPORTE("Transporte", Icons.Default.LocalShipping, Color(0xFF546E7A)),
    JARDINAGEM("Jardinagem", Icons.Default.Yard, Color(0xFF2E7D32)),
    OUTRO("Outro biscato", Icons.Default.Handyman, Color(0xFF7A5230)),
}

enum class JobStatus { ABERTO, EM_ANDAMENTO, CONCLUIDO }

data class Review(
    val reviewerName: String,
    val rating: Int,
    val comment: String,
    val date: String,
)

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
