package com.williamzabot.chatapp.ui.chatfeats.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.williamzabot.chatapp.base.BaseAuth.Companion.auth
import com.williamzabot.chatapp.base.BaseViewModel
import com.williamzabot.chatapp.model.User

class UsersViewModel : BaseViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
        .collection("users")

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val listUsers = mutableListOf<User>()

    fun getUsers() {
        listUsers.clear()
        firestore.addSnapshotListener { value, _ ->
            value?.let {
                for (doc in it.documents) {
                    val user = doc.toObject(User::class.java)
                    if (user?.id != auth.currentUser?.uid) {
                        listUsers.add(user!!)
                        _users.postValue(listUsers)
                    }
                }
            }
        }
    }

}