package com.williamzabot.chatapp.ui.chatfeats.users

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.williamzabot.chatapp.R
import com.williamzabot.chatapp.extensions.loadImage
import com.williamzabot.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class UsersAdapter(private val clickUser: (user: User) -> Unit) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    var users = listOf<User>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        context = parent.context
        return UserViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgContact = itemView.findViewById<CircleImageView>(R.id.img_contact)
        private val txtName = itemView.findViewById<TextView>(R.id.name_contact)

        fun bind(user: User) {
            txtName.text = user.fullName
            if (user.imageURL != null) {
                imgContact.loadImage(context, user.imageURL!!)
            } else {
                imgContact.background = getDrawable(context, R.drawable.camera)
            }

            itemView.setOnClickListener {
                clickUser.invoke(user)
            }
        }
    }

}