package com.williamzabot.chatapp.base

import com.google.firebase.auth.FirebaseAuth

class BaseAuth {

    companion object {
        val auth = FirebaseAuth.getInstance()
    }
}