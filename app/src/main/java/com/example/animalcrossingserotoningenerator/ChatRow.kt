package edu.cs371m.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

// Firebase demands an empty constructor, so all fields must be optional
data class ChatRow(
    var name: String? = null,
    var message: String? = null,
    var ownerUid: String? = null,
    var pictureUUID: String? = null,
    var email: String? = null,
    @ServerTimestamp val timeStamp: Timestamp? = null
)