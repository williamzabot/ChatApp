package com.williamzabot.chatapp.ui.chatfeats.conversations

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange.Type.ADDED
import com.google.firebase.firestore.FirebaseFirestore
import com.williamzabot.chatapp.R
import com.williamzabot.chatapp.base.BaseAuth.Companion.auth
import com.williamzabot.chatapp.extensions.loadImage
import com.williamzabot.chatapp.model.Contact
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class ConversationsFragment : Fragment(R.layout.fragment_conversations) {

    private val database = FirebaseFirestore.getInstance()
    private lateinit var recyclerView : RecyclerView
    private val groupAdapter = GroupAdapter<ViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerview_last_messages)
        recyclerView.adapter = groupAdapter

        val uid = auth.currentUser?.uid
        if (uid != null) {
            database.collection("lastmessages")
                .document(uid)
                .collection("contacts")
                .addSnapshotListener { value, _ ->
                    value?.documentChanges?.forEach { doc ->
                        if (doc.type == ADDED) {
                            val contact = doc.document.toObject(Contact::class.java)
                            groupAdapter.add(ContactItem(contact))
                        }
                    }
                }
        }
    }

    inner class ContactItem(private val contact: Contact) : Item() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.apply {
                findViewById<TextView>(R.id.last_message_name_contact).text = contact.name
                findViewById<TextView>(R.id.last_message_text).text = contact.lastMessage
                contact.imageUrl?.let {
                    findViewById<CircleImageView>(R.id.last_message_img_contact).loadImage(
                        requireContext(),
                        it
                    )
                }
            }
        }

        override fun getLayout() = R.layout.item_last_message

    }
}