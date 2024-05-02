package com.example.firebaseauth.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.firebaseauth.AuthResult
import com.example.firebaseauth.R
import com.example.firebaseauth.databinding.FragmentRegistrationBinding
import com.google.android.material.textfield.TextInputLayout

class RegistrationFragment : BaseAuthFragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentRegistrationBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextWatchers()
        setupButtonListeners()

        viewModel.authStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.homeFragment)
                    viewModel.clearAuthStatus()
                }

                is AuthResult.Error -> {
                    Toast.makeText(
                        context,
                        result.exception.message ?: "Registration failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                AuthResult.Loading -> binding.progressBar.visibility = View.VISIBLE
                null->null
            }
        }
    }

    private fun setupTextWatchers() {
        val clearError = { textField: TextInputLayout ->
            textField.editText?.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textField.error = null
                    binding.button1.isEnabled = true
                }

                override fun afterTextChanged(s: android.text.Editable?) {}
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
                binding.button1.isEnabled = false // Disable button while loading
                viewModel.createUserWithEmail(email, password)
            }
        }

        binding.button2.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.imageButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun validateForm(email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.textField1.error = "Email cannot be empty"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textField1.error = "Invalid email format"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.textField2.error = "Password cannot be empty"
            isValid = false
        } else if (password.length < 6) {
            binding.textField2.error = "Password must be at least 6 characters"
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            binding.textField3.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            binding.textField3.error = "Passwords do not match"
            isValid = false
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
