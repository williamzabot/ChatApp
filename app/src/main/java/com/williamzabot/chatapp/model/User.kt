package com.williamzabot.chatapp.model

import java.io.Serializable

class User() : Serializable {

    var id = ""
    var fullName = ""
    var region = ""
    var email = ""
    var imageURL: String? = null

    constructor(
        pId: String, pFullName: String, pRegion: String,
        pEmail: String, pImageUrl: String?
    ) : this() {
        id = pId
        fullName = pFullName
        region = pRegion
        email = pEmail
        imageURL = pImageUrl
    }
}