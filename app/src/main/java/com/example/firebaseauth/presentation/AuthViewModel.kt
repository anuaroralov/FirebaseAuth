package com.example.firebaseauth.presentation

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import androidx.lifecycle.viewModelScope
import com.example.firebaseauth.data.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class AuthViewModel(application: Application): AndroidViewModel(application) {

    private val auth = Firebase.auth
    private val repository = AuthRepository(auth)

    private val _resetEmailStatus = MutableLiveData<Boolean>()
    val resetEmailStatus: LiveData<Boolean> = _resetEmailStatus

    private val _signInStatus = MutableLiveData<Boolean>()
    val signInStatus: LiveData<Boolean> = _signInStatus

    private val _registrationStatus = MutableLiveData<Boolean>()
    val registrationStatus: LiveData<Boolean> = _registrationStatus

    init {
        repository.initGoogleSignInClient(application.applicationContext)
    }

    fun getGoogleSignInIntent() = repository.getGoogleSignInClient().signInIntent

    fun handleSignInResult(data: Intent?, onComplete: (Boolean) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            repository.firebaseAuthWithGoogle(account.idToken!!, onComplete)
        } catch (e: ApiException) {
            Log.w("AuthViewModel", "Google sign in failed", e)
            onComplete(false)
        }
    }

    fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        repository.sendPasswordResetEmail(email) { isSuccess ->
            _resetEmailStatus.postValue(isSuccess)
        }
    }

    fun signInWithEmail(email: String, password: String) = viewModelScope.launch {
        repository.signInWithEmail(email, password) { isSuccess ->
            _signInStatus.postValue(isSuccess)
        }
    }

    fun createUserWithEmail(email: String, password: String) = viewModelScope.launch {
        repository.createUserWithEmail(email, password) { isSuccess ->
            _registrationStatus.postValue(isSuccess)
        }
    }

    fun isUserLogin(): Boolean {
        return repository.isUserLogin()
    }

    fun logOut() {
        repository.logOut()
    }
}

