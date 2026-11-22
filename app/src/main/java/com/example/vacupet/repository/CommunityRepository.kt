package com.example.vacupet.repository

import android.util.Log
import com.example.vacupet.data.model.LostPetPost
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class CommunityRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("lost_pets")

    companion object {
        private const val TAG = "CommunityRepository"
        private const val TIMEOUT_MILLIS = 10000L // 10 segundos de timeout
    }

    suspend fun getLostPets(): List<LostPetPost> {
        return try {
            withTimeout(TIMEOUT_MILLIS) {
                val snapshot = collection.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).get().await()
                snapshot.documents.map { doc ->
                    val post = doc.toObject(LostPetPost::class.java)
                    post?.copy(id = doc.id) ?: LostPetPost()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting lost pets", e)
            emptyList()
        }
    }

    fun listenToLostPets(onUpdate: (List<LostPetPost>) -> Unit): ListenerRegistration {
        return collection.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Listen failed.", error)
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.map { doc ->
                    val post = doc.toObject(LostPetPost::class.java)
                    post?.copy(id = doc.id) ?: LostPetPost()
                } ?: emptyList()
                onUpdate(posts)
            }
    }

    suspend fun addLostPetPost(post: LostPetPost): Boolean {
        return try {
            val docRef = if (post.id.isBlank()) collection.document() else collection.document(post.id)
            val newPost = post.copy(id = docRef.id)
            withTimeout(TIMEOUT_MILLIS) {
                docRef.set(newPost).await()
            }
            Log.d(TAG, "Successfully added lost pet post: ${newPost.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding lost pet post", e)
            false
        }
    }

    suspend fun deleteLostPetPost(id: String): Boolean {
        return try {
            withTimeout(TIMEOUT_MILLIS) {
                collection.document(id).delete().await()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting lost pet post", e)
            false
        }
    }
}
