package com.example.firebaseauth.data

import com.google.firebase.auth.FirebaseAuth

class AuthRepository(private val firebaseAuth: FirebaseAuth) {

    fun sendPasswordResetEmail(email: String, onComplete: (Boolean) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

    fun signInWithEmail(email: String, password: String, onComplete: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

    fun createUserWithEmail(email: String, password: String, onComplete: (Boolean) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }
}
