package com.williamzabot.chatapp.ui.chatfeats.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction.ASCENDING
import com.williamzabot.chatapp.base.BaseAuth.Companion.auth
import com.williamzabot.chatapp.base.BaseViewModel
import com.williamzabot.chatapp.model.Contact
import com.williamzabot.chatapp.model.Message
import com.williamzabot.chatapp.model.User

class TalkViewModel : BaseViewModel() {

    private val _msg = MutableLiveData<Message>()
    val msg: LiveData<Message> = _msg

    private val _me = MutableLiveData<User>()
    val me: LiveData<User> = _me

    private val _systemError = MutableLiveData<Boolean>()
    val systemError: LiveData<Boolean> = _systemError

    private val database = FirebaseFirestore.getInstance()
    private var otherUser: User? = null

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
        database.collection("conversations")
            .document(user.id)
            .collection(otherUser.id)
            .orderBy("timestamp", ASCENDING)
            .addSnapshotListener { value, _ ->
                value?.documentChanges?.let { changes ->
                    for (doc2 in changes) {
                            val message =
                                doc2.document.toObject(Message::class.java)
                            _msg.postValue(message)

                    }
                }
            }
    }

    fun sendMessage(text: String) {
        if (otherUser != null) {
            val timestamp = System.currentTimeMillis()
            val fromId = me.value!!.id
            val toId = otherUser!!.id
            val message = Message(text, fromId, toId, timestamp)
            val contact = Contact(
                toId,
                otherUser!!.fullName,
                message.text,
                message.timestamp,
                otherUser!!.imageURL
            )
            saveInDatabase(fromId, toId, message, contact)
        } else {
            _systemError.postValue(true)
        }
    }

    private fun saveInDatabase(
        fromId: String,
        toId: String,
        message: Message,
        contact: Contact
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
                    .set(contact)
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
                    .set(contact)
            }
    }
}