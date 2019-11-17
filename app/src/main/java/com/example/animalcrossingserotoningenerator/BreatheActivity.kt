package com.example.animalcrossingserotoningenerator

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.breathe_main.*
import java.util.*

class BreatheActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.breathe_main)
        countDown()

    }

    // Count down at the start to prepare for breathing exercise
    fun countDown()
    {
        var timeLeft = 3
        breatheTV.text = "Get ready to breathe"

        var breatheTimer = object : CountDownTimer(4000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                breatheTV.text = timeLeft.toString()
                timeLeft--
            }

            override fun onFinish() {
                breatheIn()
            }
        }.start()

    }

    fun breatheIn() {
        breatheTV.text = "Breathe in through your nose"

        var breatheTimer = object : CountDownTimer(4000, 10) {

            override fun onTick(millisUntilFinished: Long) {

                var scale = (4000f - millisUntilFinished)/4000f + .1f
                // expanding the circle
                breatheTopIV.scaleX = scale
                breatheTopIV.scaleY = scale
            }

            override fun onFinish() {
                breatheTopIV.scaleX = 1.1f
                breatheTopIV.scaleY = 1.1f
                holdBreath()
            }
        }.start()
    }

    fun holdBreath() {
        breatheTV.text = "Hold"

        var breatheTimer = object : CountDownTimer(7000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                breatheOut()
            }
        }.start()
    }

    fun breatheOut() {
        breatheTV.text = "Breathe out through your mouth"

        var breatheTimer = object : CountDownTimer(8000, 10) {

            override fun onTick(millisUntilFinished: Long) {

                var scale = 1.1f - (8000f - millisUntilFinished)/8000f
                // expanding the circle
                breatheTopIV.scaleX = scale
                breatheTopIV.scaleY = scale
            }

            override fun onFinish() {
                breatheTopIV.scaleX = .1f
                breatheTopIV.scaleY = .1f
                breatheIn()
            }
        }.start()
    }
}