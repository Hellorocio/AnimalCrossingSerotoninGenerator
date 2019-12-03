package com.example.animalcrossingserotoningenerator

import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class Auth(private val activity: MainActivity) {
    companion object {
        val rcSignIn = 17
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        var user: FirebaseUser? = null
    }
    init {
        user = FirebaseAuth.getInstance().currentUser
        FirebaseAuth.AuthStateListener {
            user = FirebaseAuth.getInstance().currentUser
        }
        if (user == null) {
            // Create and launch sign-in intent
            activity.startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                        // Was creating problems
                    .setIsSmartLockEnabled(false)
                    .setAvailableProviders(providers)
                    .build(),
                rcSignIn
            )
            Log.d("XXX", "sign in attempt")

        }
    }
    fun getDisplayName(): String? {
        return user?.displayName
    }
    fun getEmail(): String? {
        return user?.email
    }
    fun getUid(): String? {
        Log.d(ChatActivity.TAG, "getUid user $user uid ${user?.uid}")
        return user?.uid
    }

    fun setDisplayName(newName : String) {
        //val user = FirebaseAuth.getInstance().currentUser
        if( user == null ) return
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()
        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user = FirebaseAuth.getInstance().currentUser
                }
            }
    }

    fun switchAccount() {
        // Create and launch sign-in intent
        FirebaseAuth.AuthStateListener {
            user = FirebaseAuth.getInstance().currentUser
        }
        activity.startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                // Was creating problems
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build(),
            rcSignIn
        )
        Log.d("XXX", "sign in attempt")
    }
}