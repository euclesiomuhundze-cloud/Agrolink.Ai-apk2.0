package com.agon.app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class ChatMessageFirestore(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)

data class ConversationFirestore(
    val id: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastTimestamp: Long = 0L,
)

fun conversationIdFor(uidA: String, uidB: String): String {
    return listOf(uidA, uidB).sorted().joinToString("_")
}

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()
    private val chatsCollection = db.collection("chats")

    private fun messagesCollection(conversationId: String) =
        chatsCollection.document(conversationId).collection("messages")

    fun observeMessages(conversationId: String): Flow<List<ChatMessageFirestore>> = callbackFlow {
        val listener = messagesCollection(conversationId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val mensagens = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<ChatMessageFirestore>()
                } ?: emptyList()
                trySend(mensagens)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(conversationId: String, senderId: String, receiverId: String, text: String) {
        val docRef = messagesCollection(conversationId).document()
        val mensagem = ChatMessageFirestore(
            id = docRef.id,
            senderId = senderId,
            text = text,
            timestamp = System.currentTimeMillis(),
        )
        docRef.set(mensagem).await()

        val conversa = ConversationFirestore(
            id = conversationId,
            participants = listOf(senderId, receiverId),
            lastMessage = text,
            lastTimestamp = System.currentTimeMillis(),
        )
        chatsCollection.document(conversationId).set(conversa).await()
    }

    fun observeConversationsFor(uid: String): Flow<List<ConversationFirestore>> = callbackFlow {
        val listener = chatsCollection
            .whereArrayContains("participants", uid)
            .orderBy("lastTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val conversas = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<ConversationFirestore>()
                } ?: emptyList()
                trySend(conversas)
            }
        awaitClose { listener.remove() }
    }
}
