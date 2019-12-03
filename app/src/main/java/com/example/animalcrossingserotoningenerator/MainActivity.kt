package com.example.animalcrossingserotoningenerator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.app.AlarmManager
import android.content.Context.ALARM_SERVICE
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders


class MainActivity : AppCompatActivity() {

    companion object {
        public lateinit var auth: Auth
    }

    private lateinit var profileViewModel : ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        auth = Auth(this)

        profileViewModel =
            ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        profileViewModel.init(auth)

        profileViewModel.getProfileInfo()
        profileViewModel.observeUsers().observe(this, Observer {
            Log.d(ChatActivity.TAG, "Observe users $it")
            var userInfo =
                it?.find { userProfile -> userProfile.email.equals(auth.getEmail()) }

            Log.d("XXX", "userinfo = $userInfo")

            if (userInfo == null){
                Log.d("XXX", "this is a new account, launching profile activity")
                // no account found, make a new one
                profileViewModel.createProfile()
                val intent = Intent(this, ProfileActivity::class.java)
                val myExtras = Bundle()
                myExtras.putBoolean("mine", true)
                myExtras.putString("email", auth.getEmail())
                intent.putExtras(myExtras)
                startActivity(intent)
            }
        })

        //TODO: don't touch the buttons until the user is verified

        breatheBut.setOnClickListener {
            val intent = Intent(this, BreatheActivity::class.java)
            startActivity(intent)
        }

        musicBut.setOnClickListener {
            val intent = Intent(this, MusicActivity::class.java)
            startActivity(intent)
        }

        inspBut.setOnClickListener {
            val intent = Intent(this, InspActivity::class.java)
            startActivity(intent)
        }

        chatBut.setOnClickListener{
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkAccountInfo () {
        var users = profileViewModel?.getUsers()
        if (profileViewModel != null && users != null) {
            var userInfo =
                users?.find { userProfile -> userProfile.email.equals(auth.getEmail()) }

            Log.d("XXX", "userinfo = $userInfo")

            if (userInfo == null){
                Log.d("XXX", "this is a new account, launching profile activity")
                // no account found, make a new one
                profileViewModel.createProfile()
                val intent = Intent(this, ProfileActivity::class.java)
                val myExtras = Bundle()
                myExtras.putBoolean("mine", true)
                myExtras.putString("email", auth.getEmail())
                intent.putExtras(myExtras)
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                val myExtras = Bundle()
                myExtras.putBoolean("mine", true)
                myExtras.putString("email", auth.getEmail())
                intent.putExtras(myExtras)
                startActivity(intent)
            }
            R.id.action_logout -> {
                //FirebaseAuth.getInstance().signOut()
                auth.switchAccount()
//                val mStartActivity = Intent(this@MainActivity, MainActivity::class.java)
//                val mPendingIntentId = 123456
//                val mPendingIntent = PendingIntent.getActivity(
//                    this@MainActivity,
//                    mPendingIntentId,
//                    mStartActivity,
//                    PendingIntent.FLAG_CANCEL_CURRENT
//                )
//                val mgr = this@MainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
//                finishAffinity()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Auth.rcSignIn) {
            //val response = IdpResponse.fromResultIntent(data)

            Log.d(javaClass.simpleName, "activity result $resultCode")
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                checkAccountInfo()

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}
