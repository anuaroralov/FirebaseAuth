package com.example.firebaseauth.presentation

import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.firebaseauth.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient


open class BaseAuthFragment : Fragment() {
    protected val viewModel: AuthViewModel by viewModels()

    private val oneTapClient: SignInClient by lazy {
        Identity.getSignInClient(requireActivity())
    }

    private lateinit var signInRequest: BeginSignInRequest

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 1001
        private const val REQ_ONE_TAP = 2
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(requireContext().getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(requireActivity()) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("BaseAuthFragment", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    showErrorDialog("One Tap Sign-in Error", "Failed to start One Tap sign-in")
                }
            }
            .addOnFailureListener(requireActivity()) { e ->
                Log.e("BaseAuthFragment", "One Tap sign-in failed: ${e.localizedMessage}")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> viewModel.handleSignInResult(data)
            REQ_ONE_TAP -> viewModel.handleOneTapSignInResult(data)
        }
    }

    protected fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    protected fun signInWithGoogle() {
        startActivityForResult(viewModel.getGoogleSignInIntent(), REQUEST_CODE_SIGN_IN)
    }
}
