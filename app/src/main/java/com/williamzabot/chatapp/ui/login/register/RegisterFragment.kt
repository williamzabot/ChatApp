package com.williamzabot.chatapp.ui.login.register

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.williamzabot.chatapp.R
import com.williamzabot.chatapp.base.BaseViewModel
import com.williamzabot.chatapp.extensions.toast
import de.hdodenhof.circleimageview.CircleImageView

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var spinnerRegion: Spinner
    private lateinit var buttonRegister: Button
    private lateinit var registerName: TextInputEditText
    private lateinit var registerEmail: TextInputEditText
    private lateinit var registerPassword: TextInputEditText
    private lateinit var imgUser: CircleImageView
    private lateinit var progressBar : ProgressBar
    private val viewModel by viewModels<RegisterViewModel>()
    private var imageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        observeEvents()
        configSpinner()
        clickButtonRegister()
        clickImgUser()
    }

    private fun init(view: View) {
        spinnerRegion = view.findViewById(R.id.spinner_region)
        buttonRegister = view.findViewById(R.id.button_register)
        registerName = view.findViewById(R.id.register_name)
        registerEmail = view.findViewById(R.id.register_email)
        registerPassword = view.findViewById(R.id.register_password)
        progressBar = view.findViewById(R.id.progressbar_register)
        imgUser = view.findViewById(R.id.img_user)
    }

    private fun observeEvents() {
        viewModel.nameFieldState.observe(viewLifecycleOwner) { nameState ->
            when (nameState) {
                is BaseViewModel.FieldState.FieldOk -> {
                    registerName.error = null
                }
                is BaseViewModel.FieldState.FieldError -> {
                    registerName.error = "Nome é obrigatório"
                }
            }
        }

        viewModel.emailFieldState.observe(viewLifecycleOwner) { emailState ->
            when (emailState) {
                is BaseViewModel.FieldState.FieldOk -> {
                    registerEmail.error = null
                }
                is BaseViewModel.FieldState.FieldError -> {
                    registerEmail.error = "Email é obrigatório"
                }
            }
        }

        viewModel.passwordFieldState.observe(viewLifecycleOwner) { passwordState ->
            when (passwordState) {
                is BaseViewModel.FieldState.FieldOk -> {
                    registerPassword.error = null
                }
                is BaseViewModel.FieldState.FieldError -> {
                    registerPassword.error = "Senha é obrigatória"
                }
            }
        }

        viewModel.successCreateUser.observe(viewLifecycleOwner) {
            toast("Sucesso ao criar usuário!")
            clearScreen()
        }

        viewModel.msgFailCreateUser.observe(viewLifecycleOwner) { msgError ->
            progressBar.visibility = GONE
            buttonRegister.isEnabled = true
            toast(msgError)
        }

        viewModel.showProgressBar.observe(viewLifecycleOwner){
            progressBar.visibility = VISIBLE
            buttonRegister.isEnabled = false
        }
    }

    private fun clickImgUser() {
        imgUser.setOnClickListener {
            startActivityForResult(Intent().apply {
                type = "image/*"
                action = ACTION_GET_CONTENT
            }, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null
        ) {
            imageUri = data.data
            imgUser.setImageURI(imageUri)
        }
    }

    private fun configSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.regions, android.R.layout.simple_spinner_dropdown_item
        )
        spinnerRegion.adapter = adapter
    }

    private fun clickButtonRegister() {
        buttonRegister.setOnClickListener {
            viewModel.checkFieldsForRegister(
                registerName.text,
                registerEmail.text,
                registerPassword.text,
                spinnerRegion.selectedItem.toString(),
                imageUri
            )
        }
    }

    private fun clearScreen() {
        registerEmail.text?.clear()
        registerName.text?.clear()
        registerPassword.text?.clear()
        imgUser.setImageDrawable(getDrawable(requireContext(), R.drawable.camera))
        progressBar.visibility = GONE
        buttonRegister.isEnabled = true
    }
}