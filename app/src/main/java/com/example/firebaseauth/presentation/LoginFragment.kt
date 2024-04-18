package com.example.firebaseauth.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.firebaseauth.R
import com.example.firebaseauth.databinding.FragmentLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("FragmentLoginBinding is null")
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextWatchers()
        setupButtonListeners()

    }

    private fun setupTextWatchers() {
        val clearError = { textField: TextInputLayout ->
            textField.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textField.error = null
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        clearError(binding.textField1)
        clearError(binding.textField2)
    }

    private fun setupButtonListeners() {
        binding.button2.setOnClickListener {
            findNavController().navigate(R.id.action_global_registrationFragment)
        }
        binding.button1.setOnClickListener {
            val email = binding.textField1.editText?.text.toString().trim()
            val password = binding.textField2.editText?.text.toString().trim()

            when {
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches() -> binding.textField1.error = "Invalid email format."

                password.isEmpty() -> binding.textField2.error = "Please enter your password."
                else -> loginUser(email, password)
            }
        }
        binding.imageButton.setOnClickListener {
            signInWithGoogle()
        }
        binding.textView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotYourPasswordFragment)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("Login", "signInWithEmail:success")
                    updateUI(auth.currentUser)
                } else {
                    handleAuthenticationError(task.exception)
                    Log.w("Login", "signInWithEmail:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun handleAuthenticationError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidCredentialsException -> binding.textField2.error =
                "Invalid password or email."

            else -> Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        user?.let {
            findNavController().navigate(R.id.action_global_homeFragment)
        }
    }

    private fun signInWithGoogle() {
        TODO("Coming soon")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
