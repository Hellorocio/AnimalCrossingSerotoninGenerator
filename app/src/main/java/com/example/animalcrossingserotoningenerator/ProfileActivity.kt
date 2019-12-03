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
        private var myfragmentManager: FragmentManager? = null
        public var root: View? = null
        public var image = ""
        public var myProfile : Boolean = false
        public var currentEmail : String? = null

        fun selectProfilePhoto(photoName : String) {

            if (root != null)
            {
                Log.d("XXX", "change photo to $photoName")
                root!!.findViewById<ImageView>(R.id.profilePicIV).setImageResource(root!!.resources.getIdentifier(photoName, "drawable", "com.example.animalcrossingserotoningenerator"))
            }

            image = photoName
            myfragmentManager?.popBackStack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quotes_frame)
        myProfile = intent.extras!!.getBoolean("mine")
        currentEmail = intent.extras!!.getString("email")

        myfragmentManager = supportFragmentManager

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
                if (currentEmail != null) {
                    var userInfo =
                        it?.find { userProfile -> userProfile.email.equals(currentEmail) }

                    if (userInfo != null) {
                        root.findViewById<ImageView>(R.id.profilePicIV).setImageResource(context!!.resources.getIdentifier(userInfo.imageName, "drawable", context!!.packageName))
                        ProfileActivity.image = userInfo.imageName!!
                        root.findViewById<EditText>(R.id.aboutMeET).setText(userInfo.aboutMe)

                        root.findViewById<Spinner>(R.id.personalityTypeSP).setSelection(userInfo.personalityType!!)
                        root.findViewById<EditText>(R.id.displayNameET).setText(userInfo.name)
                    } else {
                        // new user
                        root.findViewById<ImageView>(R.id.profilePicIV).setImageResource(context!!.resources.getIdentifier("ac_bob", "drawable", context!!.packageName))
                        ProfileActivity.image = "ac_bob"
                        root.findViewById<EditText>(R.id.aboutMeET).setText("Tell us about yourself!")

                        root.findViewById<Spinner>(R.id.personalityTypeSP).setSelection(0)
                        root.findViewById<EditText>(R.id.displayNameET).setText(MainActivity.auth.getDisplayName())
                    }
                }
            })

            if (myProfile)
            {
                root.findViewById<ImageView>(R.id.profilePicIV).setOnClickListener {
                    activity!!.supportFragmentManager.beginTransaction().add(android.R.id.content, ProfilePhotoFragment())
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit()
                }

                root.findViewById<Button>(R.id.submitBut).text = "Submit"
            }
            else
            {
                root.findViewById<Button>(R.id.submitBut).text = "Exit"
                root.findViewById<EditText>(R.id.displayNameET).isEnabled = false
                root.findViewById<EditText>(R.id.aboutMeET).isEnabled = false
                root.findViewById<Spinner>(R.id.personalityTypeSP).isEnabled = false
            }

            root.findViewById<Button>(R.id.submitBut).setOnClickListener {
                if (myProfile)
                {
                    MainActivity.auth.setDisplayName(displayNameET.text.toString())
                    var updatedUserInfo = UserInfo(root.findViewById<EditText>(R.id.aboutMeET).text.toString(),
                        MainActivity.auth.getEmail(),
                        ProfileActivity.image,
                        root.findViewById<EditText>(R.id.displayNameET).text.toString(),
                        root.findViewById<Spinner>(R.id.personalityTypeSP).selectedItemPosition)
                    profileViewModel.setProfileInfo(updatedUserInfo)
                }

                activity!!.finish()
            }

            return root
        }
    }
}
