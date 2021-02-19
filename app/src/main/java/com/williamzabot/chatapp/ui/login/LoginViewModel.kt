package com.williamzabot.chatapp.ui.login

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.williamzabot.chatapp.base.BaseAuth.Companion.auth
import com.williamzabot.chatapp.base.BaseViewModel

class LoginViewModel : BaseViewModel() {

    private val _emailFieldState = MutableLiveData<FieldState>()
    val emailFieldState: LiveData<FieldState> = _emailFieldState

    private val _passwordFieldState = MutableLiveData<FieldState>()
    val passwordFieldState: LiveData<FieldState> = _passwordFieldState

    private val _successLogin = MutableLiveData<Boolean>()
    val successLogin: LiveData<Boolean> = _successLogin

    private val _failLogin = MutableLiveData<Boolean>()
    val failLogin: LiveData<Boolean> = _failLogin

    private val _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean> = _showProgressBar

    fun checkEmailAndPasswordForLogin(email: Editable?, password: Editable?) {
        if (checkField(email)) {
            _emailFieldState.postValue(FieldState.FieldOk)
        } else {
            _emailFieldState.postValue(FieldState.FieldError)
        }
        if (checkField(password)) {
            _passwordFieldState.postValue(FieldState.FieldOk)
        } else {
            _passwordFieldState.postValue(FieldState.FieldError)
        }

        if (checkField(email) && checkField(password)) {
            _showProgressBar.postValue(true)
            login(email.toString(), password.toString())
        }
    }

    private fun login(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener {
                _successLogin.postValue(true)

            }.addOnFailureListener {
                _failLogin.postValue(true)
            }
    }
}