package com.example.firebaseauth.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebaseauth.R
import com.example.firebaseauth.databinding.FragmentRegistrationBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentRegistrationBinding is null")
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
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
        clearError(binding.textField3)
    }

    private fun setupButtonListeners() {
        binding.button1.setOnClickListener {
            val email = binding.textField1.editText?.text.toString().trim()
            val password = binding.textField2.editText?.text.toString().trim()
            val confirmPassword = binding.textField3.editText?.text.toString().trim()

            if (validateForm(email, password, confirmPassword)) {
                createAccount(email, password)
            }
        }

        binding.button2.setOnClickListener {
            findNavController().navigate(R.id.action_global_loginFragment)
        }

        binding.imageButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun validateForm(email: String, password: String, confirmPassword: String): Boolean {
        var valid = true

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textField1.error = "Invalid email format."
            valid = false
        }

        if (password.isEmpty()) {
            binding.textField2.error = "Please enter a password."
            valid = false
        } else if (password.length < 6) {
            binding.textField2.error = "Password must be at least 6 characters."
            valid = false
        }
        if (confirmPassword != password) {
            binding.textField3.error = "Passwords do not match."
            valid = false
        }

        return valid
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("Registration", "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w("Registration", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
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
