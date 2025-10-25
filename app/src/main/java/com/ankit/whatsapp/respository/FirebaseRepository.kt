package com.ankit.whatsapp.respository

import android.content.Context
import android.util.Log
import com.ankit.whatsapp.data.User
import com.ankit.whatsapp.utils.SharedPreferenceHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase,
    @ApplicationContext private val context: Context
) {

    suspend fun register(email: String, password: String, displayName: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password.trim()).await()
            val firebaseUser = result.user ?: return false

            val userData = User(
                uid = firebaseUser.uid,
                email = email,
                displayName = displayName,
                onlineStatus = true
            )

            db.reference.child("users").child(firebaseUser.uid).setValue(userData).await()
            SharedPreferenceHelper.writeUserData(context, userData)
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Register error: ${e.message}")
            false
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
            val firebaseUser = result.user ?: return false

            val snapshot = db.reference.child("users").child(firebaseUser.uid).get().await()
            val existing = snapshot.getValue(User::class.java)

            val userData = (existing ?: User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: email,
                displayName = firebaseUser.displayName ?: ""
            )).copy(onlineStatus = true) // ✅ always force online after login

            // Write back updated online status
            db.reference.child("users").child(firebaseUser.uid).setValue(userData).await()

            // Cache locally
            SharedPreferenceHelper.writeUserData(context, userData)

            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Login error: ${e.message}")
            false
        }
    }

    fun getCurrentUser() = auth.currentUser

    /** Update online status in Firebase + cache */
    suspend fun updateUserOnlineStatus(status: Boolean) {
        val uid = getCurrentUser()?.uid ?: return
        try {
            db.reference.child("users").child(uid).child("onlineStatus").setValue(status).await()
            val cached = SharedPreferenceHelper.readUserData(context)
            cached?.let {
                SharedPreferenceHelper.writeUserData(context, it.copy(onlineStatus = status))
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Status update error: ${e.message}")
        }
    }

    /** Fetch complete user data from Firebase and cache it */
    suspend fun getCurrentUserData(): User? {
        val uid = getCurrentUser()?.uid ?: return null
        return try {
            val snapshot = db.reference.child("users").child(uid).get().await()
            val user = snapshot.getValue(User::class.java)
            user?.let { SharedPreferenceHelper.writeUserData(context, it) }
            user
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Fetch error: ${e.message}")
            null
        }
    }
}

