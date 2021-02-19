package com.williamzabot.chatapp.model

import java.io.Serializable

class Contact() : Serializable {

    var id = ""
    var name = ""
    var lastMessage = ""
    var timestamp = 0L
    var imageUrl : String? = null

    constructor(pId: String, pName: String, pLastMessage: String, pTimestamp: Long, pImageUrl : String?) : this() {
        id = pId
        name = pName
        lastMessage = pLastMessage
        timestamp = pTimestamp
        imageUrl = pImageUrl
    }
}