package com.example.animalcrossingserotoningenerator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.Observer
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.profile_main.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quotes_frame)

        supportFragmentManager.beginTransaction().replace(
            android.R.id.content,
            ProfileFrag()
        ).commit()
    }

    class ProfileFrag : Fragment() {

        private lateinit var profileViewModel: ProfileViewModel

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel::class.java)
            profileViewModel.init(MainActivity.auth)
            val root = inflater.inflate(R.layout.profile_main, container, false)

            profileViewModel.getProfileInfo()

            profileViewModel.observeUsers().observe(this, Observer {
                Log.d(ChatActivity.TAG, "Observe users $it")
                if (Auth.user != null) {
                    var userInfo =
                        it?.find { userProfile -> userProfile.email.equals(Auth.user!!.email!!) }

                    if (userInfo != null) {
                        root.findViewById<ImageView>(R.id.profilePicIV).setImageResource(context!!.resources.getIdentifier(userInfo.imageName, "drawable", context!!.packageName))
                        root.findViewById<EditText>(R.id.aboutMeET).setText(userInfo.aboutMe)

                        //TODO: dropdown whatever
                    }
                    root.findViewById<EditText>(R.id.displayNameET).setText(Auth.user!!.displayName)
                }
            })

            root.findViewById<Button>(R.id.submitBut).setOnClickListener {
                val user = FirebaseAuth.getInstance().currentUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayNameET.text.toString())
                    .build()
                user?.updateProfile(profileUpdates)
                activity?.supportFragmentManager?.popBackStack()
            }
            return root
        }
    }
}
