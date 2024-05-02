package com.example.firebaseauth.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.firebaseauth.AuthResult
import com.example.firebaseauth.R
import com.example.firebaseauth.databinding.FragmentLoginBinding
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : BaseAuthFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("FragmentLoginBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextWatchers()
        setupButtonListeners()

        viewModel.authStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_global_homeFragment)
                    viewModel.clearAuthStatus()
                }

                is AuthResult.Error -> {
                    Toast.makeText(
                        context,
                        result.exception.message ?: "Login failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                AuthResult.Loading -> binding.progressBar.visibility = View.VISIBLE
                null-> null
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
    }

    private fun setupButtonListeners() {
        binding.button1.setOnClickListener {
            val email = binding.textField1.editText?.text.toString().trim()
            val password = binding.textField2.editText?.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                binding.button1.isEnabled = false
                viewModel.signInWithEmail(email, password)
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.button2.setOnClickListener {
            findNavController().navigate(R.id.action_global_registrationFragment)
        }

        binding.textView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotYourPasswordFragment)
        }

        binding.imageButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

