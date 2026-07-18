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
import com.agon.app.data.WorkerRepository
import com.agon.app.data.AuthRepository
import com.agon.app.data.ChatRepository
import com.agon.app.data.conversationIdFor
import com.agon.app.data.gemini.AgroLinkService
import com.google.firebase.auth.FirebaseUser
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

    // ---- Diagnóstico real via Gemini ----
    val isAiDiagnosing = mutableStateOf(false)
    val aiDiagnosisResult = mutableStateOf<String?>(null)
    val aiDiagnosisError = mutableStateOf<String?>(null)
    val aiDiagnosisImagePath = mutableStateOf<String?>(null)

    fun runAiDiagnosis(imageFile: java.io.File, apiKey: String, onDone: () -> Unit) {
        isAiDiagnosing.value = true
        aiDiagnosisError.value = null
        aiDiagnosisImagePath.value = imageFile.absolutePath
        viewModelScope.launch {
            val resultado = AgroLinkService.analisarPlanta(apiKey, imageFile)
            isAiDiagnosing.value = false
            if (resultado != null && !resultado.startsWith("Erro ao analisar imagem")) {
                aiDiagnosisResult.value = resultado
                onDone()
            } else {
                aiDiagnosisError.value = resultado ?: "Não foi possível analisar a imagem."
            }
        }
    }

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
    private val workerRepository = WorkerRepository()

    private val authRepository = AuthRepository()
    private val chatRepository = ChatRepository()
    val currentUser = mutableStateOf<FirebaseUser?>(authRepository.currentUser)
    val authError = mutableStateOf<String?>(null)
    val isAuthLoading = mutableStateOf(false)

    fun signUp(email: String, password: String, onSuccess: () -> Unit) {
        isAuthLoading.value = true
        authError.value = null
        viewModelScope.launch {
            val result = authRepository.signUp(email, password)
            isAuthLoading.value = false
            result.onSuccess {
                currentUser.value = it
                userName.value = email.substringBefore("@")
                onSuccess()
            }.onFailure {
                authError.value = it.message ?: "Não foi possível criar a conta."
            }
        }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        isAuthLoading.value = true
        authError.value = null
        viewModelScope.launch {
            val result = authRepository.signIn(email, password)
            isAuthLoading.value = false
            result.onSuccess {
                currentUser.value = it
                userName.value = email.substringBefore("@")
                onSuccess()
            }.onFailure {
                authError.value = it.message ?: "Não foi possível iniciar sessão."
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        currentUser.value = null
    }

    val savedWorkerIds = mutableStateListOf<String>()
    val appliedJobIds = mutableStateListOf<String>()
    val postedJobs = mutableStateListOf<JobPost>()
    val isLoadingJobs = mutableStateOf(true)
    val jobsError = mutableStateOf<String?>(null)
    val workers = mutableStateListOf<WorkerProfile>()
    val isLoadingWorkers = mutableStateOf(true)
    val workersError = mutableStateOf<String?>(null)
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
        viewModelScope.launch {
            workerRepository.observeWorkers()
                .collect { list ->
                    workers.clear()
                    workers.addAll(list)
                    isLoadingWorkers.value = false
                    workersError.value = null
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


    private val observedConversations = mutableSetOf<String>()

    fun messagesFor(workerId: String): androidx.compose.runtime.snapshots.SnapshotStateList<ChatMessage> {
        val list = chatThreads.getOrPut(workerId) {
            mutableStateListOf()
        }
        val myUid = currentUser.value?.uid
        val conversationId = if (myUid != null) conversationIdFor(myUid, workerId) else null

        if (conversationId != null && observedConversations.add(conversationId)) {
            viewModelScope.launch {
                chatRepository.observeMessages(conversationId).collect { firestoreMessages ->
                    list.clear()
                    list.addAll(
                        firestoreMessages.map { fm ->
                            ChatMessage(
                                id = fm.id,
                                senderIsMe = fm.senderId == myUid,
                                text = fm.text,
                                timeLabel = "agora",
                            )
                        }
                    )
                }
            }
        }
        return list
    }

    fun sendMessage(workerId: String, text: String) {
        val myUid = currentUser.value?.uid ?: return
        val conversationId = conversationIdFor(myUid, workerId)
        viewModelScope.launch {
            chatRepository.sendMessage(conversationId, myUid, text)
        }
    }

    fun createWorkerProfile(worker: WorkerProfile) {
        workers.add(0, worker)
        viewModelScope.launch {
            try {
                workerRepository.createProfile(worker)
            } catch (e: Exception) {
                workersError.value = "Não foi possível criar o perfil: ${e.message}"
                workers.remove(worker)
            }
        }
    }

    fun workerById(id: String): WorkerProfile? = workers.find { it.id == id }
    fun jobById(id: String): JobPost? = postedJobs.find { it.id == id }
}
