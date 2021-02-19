package com.williamzabot.chatapp.ui.login.resetpass

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.williamzabot.chatapp.base.BaseAuth.Companion.auth
import com.williamzabot.chatapp.base.BaseViewModel

class ResetPassViewModel : BaseViewModel() {

    private val _emailFieldState = MutableLiveData<FieldState>()
    val emailFieldState: LiveData<FieldState> = _emailFieldState

    private val _successResetPass = MutableLiveData<Boolean>()
    val successResetPass: LiveData<Boolean> = _successResetPass

    private val _errorReset = MutableLiveData<Boolean>()
    val errorReset: LiveData<Boolean> = _errorReset

    fun checkEmailForReset(email: Editable?) {
        if (checkField(email)) {
            _emailFieldState.postValue(FieldState.FieldOk)
        } else {
            _emailFieldState.postValue(FieldState.FieldError)
        }
        if (checkField(email)) {
            resetPass(email.toString())
        }
    }

    private fun resetPass(email: String) {
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            _successResetPass.postValue(true)
        }.addOnFailureListener {
            _errorReset.postValue(true)
        }
    }
}