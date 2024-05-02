package com.example.firebaseauth.presentation

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.firebaseauth.AuthResult
import com.example.firebaseauth.data.AuthRepository
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(Firebase.auth)

    private val _authStatus = MutableLiveData<AuthResult?>()
    val authStatus: MutableLiveData<AuthResult?> = _authStatus

    init {
        repository.initGoogleSignInClient(application.applicationContext)
    }

    fun getGoogleSignInIntent(): Intent {
        return repository.getGoogleSignInClient().signInIntent
    }

    fun handleSignInResult(data: Intent?) = viewModelScope.launch {
        _authStatus.value = AuthResult.Loading
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val idToken = account.idToken!!
            repository.firebaseAuthWithGoogleOrOneTap(idToken) { result ->
                _authStatus.value = result
            }
        } catch (e: ApiException) {
            _authStatus.value = AuthResult.Error(e)
        }
    }


    fun handleOneTapSignInResult(data: Intent?) = viewModelScope.launch {

        try {
            val credential =
                Identity.getSignInClient(getApplication<Application>().applicationContext)
                    .getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                _authStatus.value = AuthResult.Loading
                repository.firebaseAuthWithGoogleOrOneTap(idToken) { result ->
                    _authStatus.value = result
                }
            } else {
                _authStatus.value = AuthResult.Error(Exception("No ID token found."))
            }
        } catch (e: ApiException) {
            _authStatus.value = AuthResult.Error(e)
        }
    }

    fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        _authStatus.value = AuthResult.Loading
        repository.sendPasswordResetEmail(email) { result ->
            _authStatus.value = result
        }
    }

    fun signInWithEmail(email: String, password: String) = viewModelScope.launch {
        _authStatus.value = AuthResult.Loading
        repository.signInWithEmail(email, password) { result ->
            _authStatus.value = result
        }
    }

    fun createUserWithEmail(email: String, password: String) = viewModelScope.launch {
        _authStatus.value = AuthResult.Loading
        repository.createUserWithEmail(email, password) { result ->
            _authStatus.value = result
        }
    }

    fun isUserLogin(): Boolean = repository.isUserLogin()

    fun clearAuthStatus(){
        _authStatus.value = null
    }

    fun logOut() {
        repository.logOut()
    }
}
