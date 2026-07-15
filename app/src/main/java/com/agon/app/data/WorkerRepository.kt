package com.agon.app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class WorkerProfileFirestore(
    val id: String = "",
    val name: String = "",
    val skill: String = "",
    val bio: String = "",
    val location: String = "",
    val distanceKm: Double = 0.0,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val completedJobs: Int = 0,
    val priceHint: String = "",
    val verified: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
)

fun WorkerProfile.toFirestoreMap(): WorkerProfileFirestore = WorkerProfileFirestore(
    id = id,
    name = name,
    skill = skill.name,
    bio = bio,
    location = location,
    distanceKm = distanceKm,
    rating = rating,
    reviewCount = reviewCount,
    completedJobs = completedJobs,
    priceHint = priceHint,
    verified = verified,
)

fun WorkerProfileFirestore.toWorkerProfile(): WorkerProfile = WorkerProfile(
    id = id,
    name = name,
    skill = SkillCategory.entries.find { it.name == skill } ?: SkillCategory.OUTRO,
    bio = bio,
    location = location,
    distanceKm = distanceKm,
    rating = rating,
    reviewCount = reviewCount,
    completedJobs = completedJobs,
    priceHint = priceHint,
    verified = verified,
    reviews = emptyList(),
)

class WorkerRepository {

    private val db = FirebaseFirestore.getInstance()
    private val workersCollection = db.collection("workers")

    fun observeWorkers(): Flow<List<WorkerProfile>> = callbackFlow {
        val listener = workersCollection
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val workers = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<WorkerProfileFirestore>()?.toWorkerProfile()
                } ?: emptyList()
                trySend(workers)
            }
        awaitClose { listener.remove() }
    }

    suspend fun createProfile(worker: WorkerProfile) {
        val data = worker.toFirestoreMap()
        workersCollection.document(worker.id).set(data).await()
    }
}
