package com.example.firebaseauth.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.firebaseauth.databinding.FragmentForgotYourPasswordBinding
import com.google.android.material.textfield.TextInputLayout

class ForgotYourPasswordFragment : Fragment() {

    private var _binding: FragmentForgotYourPasswordBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("FragmentForgotYourPasswordBinding is null")

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

        viewModel.resetEmailStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(
                    context,
                    "If an account with that email exists, we sent you an email to reset your password.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Failed to send reset email. Please try again later.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setupTextWatchers() {
        val clearError = { textField: TextInputLayout ->
            textField.editText?.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

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
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
