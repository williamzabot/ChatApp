package com.williamzabot.chatapp.ui.chatfeats.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentChange.Type.ADDED
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction.ASCENDING
import com.williamzabot.chatapp.base.BaseAuth.Companion.auth
import com.williamzabot.chatapp.base.BaseViewModel
import com.williamzabot.chatapp.model.Contact
import com.williamzabot.chatapp.model.Message
import com.williamzabot.chatapp.model.User

class TalkViewModel : BaseViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val _me = MutableLiveData<User>()
    val me: LiveData<User> = _me

    private val _systemError = MutableLiveData<Boolean>()
    val systemError: LiveData<Boolean> = _systemError

    private val database = FirebaseFirestore.getInstance()
    private var otherUser: User? = null
    private val listMessages = mutableListOf<Message>()

    fun getUser(otherUser: User) {
        this.otherUser = otherUser
        auth.currentUser?.uid?.let {
            database.collection("users")
                .addSnapshotListener { value, _ ->
                    value?.let {
                        for (doc1 in it.documents) {
                            val user = doc1.toObject(User::class.java)
                            if (user != null && user.id == auth.currentUser?.uid) {
                                _me.postValue(user)
                                showMessages(user, otherUser)
                            }
                        }
                    }
                }
        }
    }

    private fun showMessages(
        user: User,
        otherUser: User
    ) {
        listMessages.clear()
        database.collection("conversations")
            .document(user.id)
            .collection(otherUser.id)
            .orderBy("timestamp", ASCENDING)
            .addSnapshotListener { value, _ ->
                value?.documentChanges?.let { changes ->
                    for (doc in changes) {
                        if (doc.type == ADDED) {
                            val message =
                                doc.document.toObject(Message::class.java)
                            listMessages.add(message)
                        }
                    }
                    _messages.postValue(listMessages)
                }
            }
    }

    fun sendMessage(text: String) {
        if (otherUser != null && me.value != null) {
            val timestamp = System.currentTimeMillis()
            val fromId = me.value!!.id
            val toId = otherUser!!.id
            val message = Message(text, fromId, toId, timestamp)
            val contact1 = Contact(otherUser!!, message)
            val contact2 = Contact(me.value!!, message)
            saveInDatabase(fromId, toId, message, contact1, contact2)
        } else {
            _systemError.postValue(true)
        }
    }

    private fun saveInDatabase(
        fromId: String,
        toId: String,
        message: Message,
        contact1: Contact,
        contact2: Contact
    ) {
        database.collection("conversations")
            .document(fromId)
            .collection(toId)
            .add(message)
            .addOnSuccessListener {
                database.collection("lastmessages")
                    .document(fromId)
                    .collection("contacts")
                    .document(toId)
                    .set(contact1)
            }

        database.collection("conversations")
            .document(toId)
            .collection(fromId)
            .add(message)
            .addOnSuccessListener {
                database.collection("lastmessages")
                    .document(toId)
                    .collection("contacts")
                    .document(fromId)
                    .set(contact2)
            }
    }
}