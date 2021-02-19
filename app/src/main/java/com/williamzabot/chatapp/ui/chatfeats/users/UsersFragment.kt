package com.williamzabot.chatapp.ui.chatfeats.users

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.williamzabot.chatapp.R
import com.williamzabot.chatapp.ui.chatfeats.chat.TalkActivity

const val KEY = "KEY_USER"

class UsersFragment : Fragment(R.layout.fragment_users) {

    private val viewModel by viewModels<UsersViewModel>()
    private lateinit var recyclerView: RecyclerView
    private val navController by lazy { findNavController() }
    private val usersAdapter by lazy {
        UsersAdapter { user ->
            val intent = Intent(requireContext(), TalkActivity::class.java)
            intent.putExtra(KEY, user)
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerview_users)
        recyclerView.adapter = usersAdapter
        observeEvents()
        viewModel.getUsers()
    }

    private fun observeEvents() {
        viewModel.users.observe(viewLifecycleOwner) { users ->
            usersAdapter.users = users
        }
    }

}