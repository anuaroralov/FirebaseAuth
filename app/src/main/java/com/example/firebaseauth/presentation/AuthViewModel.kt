package com.example.firebaseauth.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import androidx.lifecycle.viewModelScope
import com.example.firebaseauth.data.AuthRepository
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {

    private val auth =Firebase.auth

    private val repository= AuthRepository(auth)

    private val _resetEmailStatus = MutableLiveData<Boolean>()
    val resetEmailStatus: LiveData<Boolean> = _resetEmailStatus

    private val _signInStatus = MutableLiveData<Boolean>()
    val signInStatus: LiveData<Boolean> = _signInStatus

    private val _registrationStatus = MutableLiveData<Boolean>()
    val registrationStatus: LiveData<Boolean> = _registrationStatus

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

    fun isUserLogin():Boolean{
        return auth!=null
    }

    fun logOut(){
        auth.signOut()
    }
}
