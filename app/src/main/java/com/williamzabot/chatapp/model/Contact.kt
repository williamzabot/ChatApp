package com.williamzabot.chatapp.model

import java.io.Serializable

class Contact() : Serializable {

   var user : User? = null
   var message : Message? = null

    constructor(pUser : User, pMessage : Message) : this() {
        user = pUser
        message = pMessage
    }
}