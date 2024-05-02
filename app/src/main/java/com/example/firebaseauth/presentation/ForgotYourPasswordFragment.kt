package com.example.firebaseauth.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.firebaseauth.AuthResult
import com.example.firebaseauth.R
import com.example.firebaseauth.databinding.FragmentForgotYourPasswordBinding
import com.google.android.material.textfield.TextInputLayout

class ForgotYourPasswordFragment : Fragment() {

    private var _binding: FragmentForgotYourPasswordBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentForgotYourPasswordBinding is null")

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotYourPasswordBinding.inflate(inflater, container, false)
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
                }

                override fun afterTextChanged(s: android.text.Editable?) {}
            })
        }

        clearError(binding.textField1)
    }

    private fun setupButtonListeners() {
        binding.button1.setOnClickListener {
            val email = binding.textField1.editText?.text.toString().trim()

            if (email.isEmpty()) {
                binding.textField1.error = "Please enter your email"
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.textField1.error = "Invalid email format"
            } else {
                viewModel.sendPasswordResetEmail(email)
                binding.button1.isEnabled = false

                findNavController().popBackStack()
                Toast.makeText(
                    context,
                    "Instructions how to reset password sent to your email",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
