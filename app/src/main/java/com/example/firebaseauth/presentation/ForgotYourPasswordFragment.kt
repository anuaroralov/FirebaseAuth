package com.example.firebaseauth.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.firebaseauth.databinding.FragmentForgotYourPasswordBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ForgotYourPasswordFragment : Fragment() {

    private var _binding: FragmentForgotYourPasswordBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("FragmentForgotYourPasswordBinding is null")
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotYourPasswordBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button1.setOnClickListener {
            val email = binding.textField1.editText?.text.toString().trim()
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.textField1.error = "Введите действительный email"
            } else {
                sendPasswordResetEmail(email)
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Если аккаунт с таким email существует, инструкции по сбросу пароля были отправлены.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Произошла ошибка. Пожалуйста, проверьте ваше интернет-соединение и попробуйте снова.", Toast.LENGTH_LONG).show()
                }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
