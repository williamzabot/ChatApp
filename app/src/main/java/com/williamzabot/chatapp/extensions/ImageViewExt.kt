package com.williamzabot.chatapp.extensions

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Picasso

fun ImageView.loadImage(context: Context, url: String) {
    Picasso.with(context).load(url).fit().centerInside().into(this)
}