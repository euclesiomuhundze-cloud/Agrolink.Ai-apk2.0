package com.agon.app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class JobPostFirestore(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val description: String = "",
    val location: String = "",
    val distanceKm: Double = 0.0,
    val budgetHint: String = "",
    val postedBy: String = "",
    val postedAgo: String = "",
    val urgent: Boolean = false,
    val status: String = "ABERTO",
    val timestamp: Long = System.currentTimeMillis(),
)

fun JobPost.toFirestoreMap(): JobPostFirestore = JobPostFirestore(
    id = id,
    title = title,
    category = category.name,
    description = description,
    location = location,
    distanceKm = distanceKm,
    budgetHint = budgetHint,
    postedBy = postedBy,
    postedAgo = postedAgo,
    urgent = urgent,
    status = status.name,
)

fun JobPostFirestore.toJobPost(): JobPost = JobPost(
    id = id,
    title = title,
    category = SkillCategory.entries.find { it.name == category } ?: SkillCategory.OUTRO,
    description = description,
    location = location,
    distanceKm = distanceKm,
    budgetHint = budgetHint,
    postedBy = postedBy,
    postedAgo = postedAgo,
    urgent = urgent,
    status = JobStatus.entries.find { it.name == status } ?: JobStatus.ABERTO,
)

class JobRepository {

    private val db = FirebaseFirestore.getInstance()
    private val jobsCollection = db.collection("jobs")

    fun observeJobs(): Flow<List<JobPost>> = callbackFlow {
        val listener = jobsCollection
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val jobs = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<JobPostFirestore>()?.toJobPost()
                } ?: emptyList()
                trySend(jobs)
            }
        awaitClose { listener.remove() }
    }

    suspend fun postJob(job: JobPost) {
        val data = job.toFirestoreMap()
        jobsCollection.document(job.id).set(data).await()
    }

    suspend fun updateJobStatus(jobId: String, status: JobStatus) {
        jobsCollection.document(jobId).update("status", status.name).await()
    }

    suspend fun deleteJob(jobId: String) {
        jobsCollection.document(jobId).delete().await()
    }
}
