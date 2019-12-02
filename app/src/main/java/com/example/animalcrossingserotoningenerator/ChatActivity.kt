package com.example.animalcrossingserotoningenerator

import android.R.id
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth

class ChatActivity : AppCompatActivity() {

    companion object {
        val TAG = "FireDemo"
    }
    private lateinit var viewModel: ChatViewModel
    private lateinit var auth: Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_main)


        auth = MainActivity.auth
        viewModel = ViewModelProviders.of(this)[ChatViewModel::class.java]
        viewModel.init(auth, Storage(resources))


        val user = FirebaseAuth.getInstance().currentUser

        supportFragmentManager.beginTransaction().replace(
            id.content,
            ChatFragment()
        ).commit()
    }

}
