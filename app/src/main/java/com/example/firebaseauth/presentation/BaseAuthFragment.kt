package com.example.firebaseauth.presentation

import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.firebaseauth.R

open class BaseAuthFragment : Fragment() {

    protected val viewModel: AuthViewModel by viewModels()

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 1001
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            viewModel.handleSignInResult(data) { isSuccess ->
                if (isSuccess) {
                    Toast.makeText(context, "Google sign in successful", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_global_homeFragment)
                } else {
                    Toast.makeText(context, "Google sign in failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun signInWithGoogle() {
        startActivityForResult(viewModel.getGoogleSignInIntent(), REQUEST_CODE_SIGN_IN)
    }
}
