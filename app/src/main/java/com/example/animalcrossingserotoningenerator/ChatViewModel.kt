package com.example.animalcrossingserotoningenerator

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animalcrossingserotoningenerator.api.ProfanityFilterApi
import com.example.animalcrossingserotoningenerator.api.ProfanityFilterRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import edu.cs371m.firestore.ChatRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatViewModel : ViewModel() {
    private lateinit var db: FirebaseFirestore
    private var auth: Auth? = null
    public lateinit var storage: Storage
    private var chat = MutableLiveData<List<ChatRow>>()
    private var chatListener : ListenerRegistration? = null
    // Ouch, this is a very poor man's cache
    private var uuid2localpath = mutableMapOf<String,String>()
    private val profApi = ProfanityFilterApi.create()
    private val profFilter = ProfanityFilterRepository(profApi)
    private var filteredText = MutableLiveData<String>().apply { "" }

    private var users =  MutableLiveData<List<UserInfo>>()

    fun init(auth: Auth, storage: Storage) {
        db = FirebaseFirestore.getInstance()
        if (db == null) {
            Log.d(ChatActivity.TAG, "XXX FirebaseFirestore is null!")
        }
        this.auth = auth
        this.storage = storage
    }
    fun getDisplayName(): String? {
        return auth?.getDisplayName()
    }
    fun getEmail(): String? {
        return auth?.getEmail()
    }
    fun getUid(): String? {
        return auth?.getUid()
    }
    fun observeChat(): LiveData<List<ChatRow>> {
        return chat
    }

    fun getChatSize(): Int {
        if(chat.value == null){
            return 0
        } else {
            return chat.value!!.size
        }
    }

    fun saveChatRow(chatRow: ChatRow) {
        Log.d(
            "HomeViewModel",
            String.format(
                "saveChatRow ownerUid(%s) name(%s) %s",
                chatRow.ownerUid,
                chatRow.name,
                chatRow.message
            )
        )

        db.collection("globalChat").add(chatRow)
        
    }

    fun filterText(text: String) = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO) {
        // Update LiveData from IO dispatcher, use postValue
        filteredText.postValue(profFilter.filterText(text))
    }
    fun observeFilterText() : LiveData<String> {
        return filteredText
    }

    fun getUsers() {
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

    fun getProfilePhoto(email : String) : String {
        if (users.value != null) {
            var userInfo = users.value!!.find { userProfile -> userProfile.email.equals(email) }
            if (userInfo != null && userInfo.imageName != null) {
                return userInfo.imageName!!
            }
        }
        return "ac_bob"
    }

    fun getChat()  {
        // Limit total number of chat rows to 100
        db.collection("globalChat").limit(100).orderBy("timeStamp").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Log.w(ChatActivity.TAG, "listen:error", firebaseFirestoreException)
                return@addSnapshotListener
            }
            Log.d(ChatActivity.TAG, "fetch ${querySnapshot!!.documents.size}")
            chat.value = querySnapshot.documents.mapNotNull {
                it.toObject(ChatRow::class.java)
            }
        }
    }

    fun uploadJpg(localPath: String, uuid: String) {
        storage.uploadJpg(localPath, uuid)
    }
    // Very poor man's cache.  I should really use glide
    fun downloadJpg(uuid: String, textView: TextView) {
        if(uuid2localpath.containsKey(uuid)) {
            Log.d(ChatActivity.TAG, "local load $uuid")
            storage.loadFileToTV(uuid, textView)
        } else {
            Log.d(ChatActivity.TAG, "remote load $uuid")
            uuid2localpath[uuid] = storage.downloadJpg(uuid, textView)
        }
    }

    // Debateable how useful this is.
    override fun onCleared() {
        super.onCleared()
        chatListener?.remove()
    }
}