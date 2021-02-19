package com.williamzabot.chatapp.base

import android.text.Editable
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

open class BaseViewModel : ViewModel() {

    protected fun checkField(editable: Editable?): Boolean {
        return !editable.isNullOrBlank()
    }

    sealed class FieldState {
        object FieldError : FieldState()
        object FieldOk : FieldState()
    }
}