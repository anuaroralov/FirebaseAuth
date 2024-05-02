package com.example.firebaseauth

sealed class AuthResult {
    data object Success : AuthResult()
    data class Error(val exception: Exception) : AuthResult()
    data object Loading : AuthResult()
}
