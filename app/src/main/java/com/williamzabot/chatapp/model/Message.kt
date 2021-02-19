package com.williamzabot.chatapp.model

import java.io.Serializable

class Message() : Serializable {
    var text = ""
    var fromId = ""
    var toId = ""
    var timestamp = 0L

    constructor(pText: String, pFromId: String, pToId: String, pTimestamp: Long) : this() {
        text = pText
        fromId = pFromId
        toId = pToId
        timestamp = pTimestamp
    }
}