package com.example.userstoryapp.ui.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.userstoryapp.R
import com.example.userstoryapp.databinding.FragmentLoginBinding
import com.example.userstoryapp.ui.activities.AuthActivity
import com.example.userstoryapp.utils.dataStore
import com.example.userstoryapp.ui.viewmodels.AuthViewModel
import com.example.userstoryapp.ui.viewmodels.SettingViewModel
import com.example.userstoryapp.ui.viewmodels.SettingViewModelFactory
import com.example.userstoryapp.utils.SettingPreferences

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by activityViewModels()

    companion object {
        fun newInstance() = LoginFragment()
    }

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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val settingPref = SettingPreferences.getInstance((activity as AuthActivity).dataStore)
        val settingViewModel = ViewModelProvider(
            this,
            SettingViewModelFactory(settingPref)
        )[SettingViewModel::class.java]

        viewModel.let {
            it.loginResult.observe(viewLifecycleOwner) { login ->
                settingViewModel.setUserPreferences(
                    login.loginResult.token,
                    login.loginResult.userId,
                    login.loginResult.name,
                    viewModel.tempEmail.value ?: "Not Set"
                )
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

        settingViewModel.getUserPreferences(SettingPreferences.Companion.UserPreferences.UserToken.name)
            .observe(viewLifecycleOwner) { token ->
                if (token != "Not Set") (activity as AuthActivity).toMainActivity()
            }

        binding.btnSignin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() or password.isEmpty() -> {
                    binding.edLoginEmail.requestFocus()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.validation_empty_email_password),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                !email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) -> {
                    binding.edLoginEmail.requestFocus()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.validation_invalid_email),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                password.length < 8 -> {
                    binding.edLoginPassword.requestFocus()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.validation_invalid_password),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    viewModel.login(email, password)
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.container, RegisterFragment(), RegisterFragment::class.java.simpleName)
                addSharedElement(binding.labelLogin, "auth")
                addSharedElement(binding.edLoginEmail, "email")
                addSharedElement(binding.edLoginPassword, "password")
                addSharedElement(binding.btnSignin, "btn_login_register")
                addSharedElement(binding.containerRegister, "misc")
                commit()
            }
        }
    }
}