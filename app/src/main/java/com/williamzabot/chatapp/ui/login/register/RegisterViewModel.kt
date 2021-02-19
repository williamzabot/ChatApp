package com.williamzabot.chatapp.ui.login.register

import android.net.Uri
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.williamzabot.chatapp.base.BaseAuth.Companion.auth
import com.williamzabot.chatapp.base.BaseViewModel
import com.williamzabot.chatapp.model.User
import java.util.*

class RegisterViewModel : BaseViewModel() {

    private val _nameFieldState = MutableLiveData<FieldState>()
    val nameFieldState: LiveData<FieldState> = _nameFieldState

    private val _emailFieldState = MutableLiveData<FieldState>()
    val emailFieldState: LiveData<FieldState> = _emailFieldState

    private val _passwordFieldState = MutableLiveData<FieldState>()
    val passwordFieldState: LiveData<FieldState> = _passwordFieldState

    private val _successCreateUser = MutableLiveData<Boolean>()
    val successCreateUser: LiveData<Boolean> = _successCreateUser

    private val _msgFailCreateUser = MutableLiveData<String>()
    val msgFailCreateUser: LiveData<String> = _msgFailCreateUser

    private val _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean> = _showProgressBar

    private val firestore = FirebaseFirestore.getInstance()
        .collection("users")

    fun checkFieldsForRegister(
        name: Editable?,
        email: Editable?,
        password: Editable?,
        region: String,
        imageUri: Uri?
    ) {
        if (checkField(name)) {
            _nameFieldState.postValue(FieldState.FieldOk)
        } else {
            _nameFieldState.postValue(FieldState.FieldError)
        }
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

        if (checkField(name) && checkField(email) && checkField(password)) {
            _showProgressBar.postValue(true)
            createUser(name.toString(), email.toString(), password.toString(), region, imageUri)
        }
    }

    private fun createUser(
        name: String,
        email: String,
        password: String,
        region: String,
        imageUri: Uri?
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            if (imageUri != null) {
                val storage =
                    FirebaseStorage.getInstance().getReference("/images/${UUID.randomUUID()}")
                storage.putFile(imageUri).addOnSuccessListener {
                    storage.downloadUrl.addOnSuccessListener {
                        saveUser(name, region, it)
                    }.addOnFailureListener {
                        saveUser(name, region, null)
                    }
                }.addOnFailureListener {
                    saveUser(name, region, null)
                }
            } else {
                saveUser(name, region, null)
            }
        }.addOnFailureListener {
            answerOptions(it.toString())
        }
    }

    private fun saveUser(name: String, region: String, downloadUrl: Uri?) {
        val id = auth.currentUser?.uid
        if (id != null) {
            val user = User(
                id,
                name,
                region,
                auth.currentUser?.email ?: "",
                downloadUrl.toString()
            )
            firestore
                .document(id)
                .set(user)
                .addOnSuccessListener {
                    _successCreateUser.postValue(true)
                }.addOnFailureListener {
                    _msgFailCreateUser.postValue(it.message.toString())
                }
        } else {
            _msgFailCreateUser.postValue("Falha ao criar usuário")
        }
    }

    private fun answerOptions(answer: String) {
        when {
            answer.contains("least 6 characters") -> {
                _msgFailCreateUser.postValue("Não é permitido senha menor do que 6 digitos")
            }
            answer.contains("address is badly") -> {
                _msgFailCreateUser.postValue("Email inválido!")
            }
            answer.contains("interrupted connection") -> {
                _msgFailCreateUser.postValue("Sem conexão com a Internet")
            }
            answer.contains("address is already") -> {
                _msgFailCreateUser.postValue("Email já cadastrado")
            }
        }
    }
}