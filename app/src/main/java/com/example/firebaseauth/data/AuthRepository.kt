package com.example.firebaseauth.data

import android.content.Context
import com.example.firebaseauth.AuthResult
import com.example.firebaseauth.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthRepository(private val firebaseAuth: FirebaseAuth) {

    private lateinit var googleSignInClient: GoogleSignInClient

    fun initGoogleSignInClient(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getGoogleSignInClient(): GoogleSignInClient = googleSignInClient

    fun firebaseAuthWithGoogleOrOneTap(idToken: String, onComplete: (AuthResult) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(AuthResult.Success)
                } else {
                    onComplete(
                        AuthResult.Error(
                            task.exception ?: Exception("Unknown authentication error")
                        )
                    )
                }
            }
    }

    fun sendPasswordResetEmail(email: String, onComplete: (AuthResult) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(AuthResult.Success)
            } else {
                onComplete(
                    AuthResult.Error(
                        task.exception ?: Exception("Failed to send password reset email")
                    )
                )
            }
        }
    }

    fun signInWithEmail(email: String, password: String, onComplete: (AuthResult) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(AuthResult.Success)
            } else {
                onComplete(
                    AuthResult.Error(
                        task.exception ?: Exception("Failed to sign in with email and password")
                    )
                )
            }
        }
    }

    fun createUserWithEmail(email: String, password: String, onComplete: (AuthResult) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(AuthResult.Success)
            } else {
                onComplete(
                    AuthResult.Error(
                        task.exception ?: Exception("Failed to create user with email and password")
                    )
                )
            }
        }
    }

    fun isUserLogin(): Boolean = firebaseAuth.currentUser != null

    fun logOut() {
        firebaseAuth.signOut()
    }
}
