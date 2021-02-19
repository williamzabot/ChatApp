package com.williamzabot.chatapp.ui.chatfeats.chat

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.williamzabot.chatapp.R
import com.williamzabot.chatapp.base.BaseAuth.Companion.auth
import com.williamzabot.chatapp.extensions.loadImage
import com.williamzabot.chatapp.model.Message
import com.williamzabot.chatapp.model.User
import com.williamzabot.chatapp.ui.chatfeats.ChatActivity
import com.williamzabot.chatapp.ui.chatfeats.users.KEY
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class TalkActivity : AppCompatActivity() {

    private lateinit var otherUser: User
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerview_messages) }
    private val buttonSend by lazy { findViewById<ImageButton>(R.id.button_send_message) }
    private val editTextChat by lazy { findViewById<EditText>(R.id.edittext_chat) }
    private val groupAdapter = GroupAdapter<ViewHolder>()
    private lateinit var viewModel: TalkViewModel
    private var myId = auth.currentUser?.uid
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_talk)
        viewModel = ViewModelProvider(this).get(TalkViewModel::class.java)
        observeEvents()
        otherUser = intent.getSerializableExtra(KEY) as User
        configToolbar()
        recyclerView.adapter = groupAdapter
        viewModel.getUser(otherUser)
        clickSendButton()
    }

    private fun observeEvents() {
        viewModel.msg.observe(this) { msg ->
            groupAdapter.add(MessageItem(msg))
        }

        viewModel.systemError.observe(this) {
            Toast.makeText(this, "Erro de sistema", Toast.LENGTH_LONG).show()
        }

        viewModel.me.observe(this) {
            user = it
        }
    }

    private fun clickSendButton() {
        buttonSend.setOnClickListener {
            val text = editTextChat.text
            if (text.isNotEmpty()) {
                viewModel.sendMessage(text.toString())
                text.clear()
            }
        }
    }

    private fun configToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.talk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = otherUser.fullName
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            goToChatActivity()
        }
    }

    override fun onBackPressed() {
        goToChatActivity()
    }

    private fun goToChatActivity() {
        startActivity(Intent(this, ChatActivity::class.java))
        finish()
    }

    inner class MessageItem(private val message: Message) : Item() {

        override fun bind(viewHolder: ViewHolder, position: Int) {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            val hour = format.format(message.timestamp)

            viewHolder.itemView.apply {
                findViewById<TextView>(R.id.text_message).text = message.text
                findViewById<TextView>(R.id.text_hour).text = hour

                if (message.fromId == myId) {
                    user?.imageURL
                } else {
                    otherUser.imageURL
                }?.let {
                    findViewById<CircleImageView>(R.id.message_img_user).loadImage(
                        this@TalkActivity, it
                    )
                }
            }
        }

        override fun getLayout(): Int {
            return if (message.fromId == myId) {
                R.layout.my_message
            } else {
                R.layout.message_from_user
            }
        }
    }
}