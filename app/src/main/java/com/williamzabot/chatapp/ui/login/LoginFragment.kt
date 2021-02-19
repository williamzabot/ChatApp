package com.williamzabot.chatapp.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.williamzabot.chatapp.R
import com.williamzabot.chatapp.base.BaseViewModel
import com.williamzabot.chatapp.extensions.toast
import com.williamzabot.chatapp.ui.chatfeats.ChatActivity

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var buttonLogin: Button
    private lateinit var txtReset: TextView
    private lateinit var txtRegister: TextView
    private lateinit var loginEmail: TextInputEditText
    private lateinit var loginPass: TextInputEditText
    private lateinit var progressBar : ProgressBar
    private val navController by lazy { findNavController() }
    private val viewModel by viewModels<LoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        observeEvents()
        clickEvents()
    }

    private fun observeEvents() {
        viewModel.emailFieldState.observe(viewLifecycleOwner) { emailState ->
            when (emailState) {
                is BaseViewModel.FieldState.FieldOk -> {
                    loginEmail.error = null
                }
                is BaseViewModel.FieldState.FieldError -> {
                    loginEmail.error = "Email é obrigatório"
                }
            }
        }

        viewModel.passwordFieldState.observe(viewLifecycleOwner) { passwordState ->
            when (passwordState) {
                is BaseViewModel.FieldState.FieldOk -> {
                    loginPass.error = null
                }
                is BaseViewModel.FieldState.FieldError -> {
                    loginPass.error = "Senha é obrigatória"
                }
            }
        }

        viewModel.successLogin.observe(viewLifecycleOwner) {
            val intent = Intent(requireActivity(), ChatActivity::class.java)
            startActivity(intent)
            progressBar.visibility = GONE
            buttonLogin.isEnabled = true
            activity?.finish()
        }

        viewModel.failLogin.observe(viewLifecycleOwner) {
            progressBar.visibility = GONE
            buttonLogin.isEnabled = true
            toast("Email e senha não correspondem!")
        }

        viewModel.showProgressBar.observe(viewLifecycleOwner){
            progressBar.visibility = VISIBLE
            buttonLogin.isEnabled = false
        }

    }

    private fun init(view: View) {
        progressBar = view.findViewById(R.id.progressbar_login)
        buttonLogin = view.findViewById(R.id.button_login)
        loginEmail = view.findViewById(R.id.login_email)
        loginPass = view.findViewById(R.id.login_password)
        txtRegister = view.findViewById(R.id.txt_register)
        txtReset = view.findViewById(R.id.txt_reset)
    }

    private fun clickEvents() {
        clickButtonLogin()
        clickButtonRegister()
        clickButtonReset()
    }

    private fun clickButtonReset() {
        txtReset.setOnClickListener {
            navController.navigate(R.id.action_login_to_resetpass)
        }
    }

    private fun clickButtonRegister() {
        txtRegister.setOnClickListener {
            navController.navigate(R.id.action_login_to_register)
        }
    }

    private fun clickButtonLogin() {
        buttonLogin.setOnClickListener {
            viewModel.checkEmailAndPasswordForLogin(loginEmail.text, loginPass.text)
        }
    }
}