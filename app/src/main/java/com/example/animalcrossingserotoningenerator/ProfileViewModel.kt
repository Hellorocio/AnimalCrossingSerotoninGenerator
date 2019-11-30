package com.example.animalcrossingserotoningenerator

import android.security.keystore.UserPresenceUnavailableException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import edu.cs371m.firestore.ChatRow

class ProfileViewModel : ViewModel() {

    private lateinit var db: FirebaseFirestore
    private var auth: Auth? = null
    private var users = MutableLiveData<List<UserInfo>>()
    private val _text = MutableLiveData<String>().apply {
        value = "This is profile Fragment"
    }
    val text: LiveData<String> = _text
    fun init(auth: Auth) {
        db = FirebaseFirestore.getInstance()
        if (db == null) {
            Log.d(ChatActivity.TAG, "XXX FirebaseFirestore is null!")
        }
        this.auth = auth
    }

    fun observeUsers(): LiveData<List<UserInfo>> {
        return users
    }

    fun getProfileInfo() {
        db.collection("users").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Log.w(ChatActivity.TAG, "listen:error", firebaseFirestoreException)
                return@addSnapshotListener
            }
            Log.d(ChatActivity.TAG, "fetch ${querySnapshot!!.documents.size}")

            users.value = querySnapshot.documents.mapNotNull {
                it.toObject(UserInfo::class.java)
            }
        }
    }
}