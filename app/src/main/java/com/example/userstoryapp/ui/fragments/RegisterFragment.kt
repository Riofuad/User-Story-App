package com.example.userstoryapp.ui.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.userstoryapp.R
import com.example.userstoryapp.databinding.FragmentRegisterBinding
import com.example.userstoryapp.ui.viewmodels.AuthViewModel

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.let {
            it.loginResult.observe(viewLifecycleOwner) { register ->
                if (!register.error) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.successful_register_user),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            it.error.observe(viewLifecycleOwner) { error ->
                if (error.isNotEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_login),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            it.isLoading.observe(viewLifecycleOwner) { loading ->
                binding.progressBar.visibility = loading
            }
        }

        binding.btnSignup.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {
                email.isEmpty() or password.isEmpty() -> {
                    binding.edRegisterEmail.requestFocus()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.validation_empty_email_password),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                !email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) -> {
                    binding.edRegisterEmail.requestFocus()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.validation_invalid_email),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                password.length < 8 -> {
                    binding.edRegisterPassword.requestFocus()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.validation_invalid_password),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    viewModel.register(name, email, password)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.successful_register_user),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.container, LoginFragment(), LoginFragment::class.java.simpleName)
                addSharedElement(binding.labelRegister, "auth")
                addSharedElement(binding.edRegisterEmail, "email")
                addSharedElement(binding.edRegisterPassword, "password")
                addSharedElement(binding.btnSignup, "btn_login_register")
                addSharedElement(binding.containerRegister, "misc")
                commit()
            }
        }
    }
}