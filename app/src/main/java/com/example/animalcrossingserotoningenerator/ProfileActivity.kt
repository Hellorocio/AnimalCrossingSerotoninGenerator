package com.example.animalcrossingserotoningenerator

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.Observer
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.profile_main.*

class ProfileActivity : AppCompatActivity() {

    companion object {
        private var fragmentManager: FragmentManager? = null
        public var root: View? = null
        public var image = ""

        fun selectProfilePhoto(photoName : String) {

            if (root != null)
            {
                Log.d("XXX", "change photo to $photoName")
                root!!.findViewById<ImageView>(R.id.profilePicIV).setImageResource(root!!.resources.getIdentifier(photoName, "drawable", "com.example.animalcrossingserotoningenerator"))
            }

            image = photoName
            fragmentManager?.popBackStack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quotes_frame)

        Companion.fragmentManager = supportFragmentManager

        supportFragmentManager.beginTransaction().replace(
            android.R.id.content,
            ProfileFrag()
        ).commit()
    }

    class ProfileFrag : Fragment() {

        private lateinit var profileViewModel: ProfileViewModel

        private val personalityTypes: Array<String> by lazy {
            resources.getStringArray(R.array.personality_type)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel::class.java)
            profileViewModel.init(MainActivity.auth)
            val root = inflater.inflate(R.layout.profile_main, container, false)
            ProfileActivity.root = root

            val personalityTypeAdapter = ArrayAdapter.createFromResource(activity!!,
                R.array.personality_type,
                android.R.layout.simple_spinner_item)
            personalityTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            root.findViewById<Spinner>(R.id.personalityTypeSP).adapter = personalityTypeAdapter

            profileViewModel.getProfileInfo()

            profileViewModel.observeUsers().observe(this, Observer {
                Log.d(ChatActivity.TAG, "Observe users $it")
                if (Auth.user != null) {
                    var userInfo =
                        it?.find { userProfile -> userProfile.email.equals(Auth.user!!.email!!) }

                    if (userInfo != null) {
                        root.findViewById<ImageView>(R.id.profilePicIV).setImageResource(context!!.resources.getIdentifier(userInfo.imageName, "drawable", context!!.packageName))
                        ProfileActivity.image = userInfo.imageName!!
                        root.findViewById<EditText>(R.id.aboutMeET).setText(userInfo.aboutMe)

                        root.findViewById<Spinner>(R.id.personalityTypeSP).setSelection(userInfo.personalityType!!)
                    }
                    root.findViewById<EditText>(R.id.displayNameET).setText(Auth.user!!.displayName)
                }
            })

            root.findViewById<Button>(R.id.submitBut).setOnClickListener {
                MainActivity.auth.setDisplayName(displayNameET.text.toString())
                var updatedUserInfo = UserInfo(root.findViewById<EditText>(R.id.aboutMeET).text.toString(),
                    MainActivity.auth.getEmail(),
                    ProfileActivity.image,
                    root.findViewById<Spinner>(R.id.personalityTypeSP).selectedItemPosition)
                profileViewModel.setProfileInfo(updatedUserInfo)

                activity!!.finish()
            }

            root.findViewById<ImageView>(R.id.profilePicIV).setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction().add(android.R.id.content, ProfilePhotoFragment())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
            }

            return root
        }
    }
}
