package com.example.firebaseauth.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.example.firebaseauth.R


class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<AuthViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment?
        val navController = navHostFragment?.navController

        if (viewModel.isUserLogin() != null && navController != null) {
            val actionId = R.id.action_global_homeFragment
            navController.navigate(actionId)
        }
    }
}
