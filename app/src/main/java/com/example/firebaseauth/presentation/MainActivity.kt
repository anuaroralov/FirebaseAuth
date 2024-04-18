package com.example.firebaseauth.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.example.firebaseauth.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment?
        val navController = navHostFragment?.navController

        val auth = Firebase.auth
        if (auth.currentUser != null && navController != null) {
            val actionId = R.id.action_global_homeFragment
            navController.navigate(actionId)
        }
    }
}
