package com.agon.app.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agon.app.data.ChatMessage
import com.agon.app.data.DiagnosisRecord
import com.agon.app.data.JobPost
import com.agon.app.data.JobRepository
import com.agon.app.data.JobStatus
import com.agon.app.data.SampleData
import com.agon.app.data.SampleLeaf
import com.agon.app.data.WorkerProfile
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppLanguage(val label: String) {
    PORTUGUES("Português"),
    UMBUNDU("Umbundu"),
    KIMBUNDU("Kimbundu"),
    LINGALA("Lingala"),
}

class AppViewModel : ViewModel() {

    val offlineMode = mutableStateOf(false)
    val language = mutableStateOf(AppLanguage.PORTUGUES)
    val notificationsEnabled = mutableStateOf(true)
    val userName = mutableStateOf("Agricultor(a) & Trabalhador(a)")

    val diagnosisHistory = mutableStateListOf<DiagnosisRecord>()
    val isDiagnosing = mutableStateOf(false)
    val lastDiagnosis = mutableStateOf<DiagnosisRecord?>(null)

    fun runDiagnosis(leaf: SampleLeaf, onDone: (DiagnosisRecord) -> Unit) {
        val diseaseId = leaf.diseaseId ?: "healthy"
        val disease = SampleData.diseaseById(diseaseId)
        val record = DiagnosisRecord(
            id = UUID.randomUUID().toString(),
            leaf = leaf,
            disease = disease,
            timestamp = System.currentTimeMillis(),
        )
        diagnosisHistory.add(0, record)
        lastDiagnosis.value = record
        onDone(record)
    }

    private val jobRepository = JobRepository()

    val savedWorkerIds = mutableStateListOf<String>()
    val appliedJobIds = mutableStateListOf<String>()
    val postedJobs = mutableStateListOf<JobPost>()
    val isLoadingJobs = mutableStateOf(true)
    val jobsError = mutableStateOf<String?>(null)
    val chatThreads = mutableMapOf<String, androidx.compose.runtime.snapshots.SnapshotStateList<ChatMessage>>()

    init {
        viewModelScope.launch {
            jobRepository.observeJobs()
                .collect { jobs ->
                    postedJobs.clear()
                    postedJobs.addAll(jobs)
                    isLoadingJobs.value = false
                    jobsError.value = null
                }
        }
    }

    fun toggleSavedWorker(id: String) {
        if (savedWorkerIds.contains(id)) savedWorkerIds.remove(id) else savedWorkerIds.add(id)
    }

    fun isWorkerSaved(id: String) = savedWorkerIds.contains(id)

    fun applyToJob(jobId: String) {
        if (!appliedJobIds.contains(jobId)) appliedJobIds.add(jobId)
    }

    fun hasApplied(jobId: String) = appliedJobIds.contains(jobId)

    fun postNewJob(job: JobPost) {
        postedJobs.add(0, job)
        viewModelScope.launch {
            try {
                jobRepository.postJob(job)
            } catch (e: Exception) {
                jobsError.value = "Não foi possível publicar o biscato: ${e.message}"
                postedJobs.remove(job)
            }
        }
    }

    fun messagesFor(workerId: String): androidx.compose.runtime.snapshots.SnapshotStateList<ChatMessage> {
        return chatThreads.getOrPut(workerId) {
            mutableStateListOf(*SampleData.sampleChat.toTypedArray())
        }
    }

    fun sendMessage(workerId: String, text: String) {
        val list = messagesFor(workerId)
        list.add(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                senderIsMe = true,
                text = text,
                timeLabel = "agora",
            ),
        )
        list.add(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                senderIsMe = false,
                text = "Combinado, obrigado pela mensagem! Vou confirmar em breve.",
                timeLabel = "agora",
            ),
        )
    }

    fun workerById(id: String): WorkerProfile? = SampleData.workers.find { it.id == id }
    fun jobById(id: String): JobPost? = postedJobs.find { it.id == id }
}
