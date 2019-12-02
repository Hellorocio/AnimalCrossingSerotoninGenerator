package com.example.animalcrossingserotoningenerator

import android.graphics.drawable.AnimationDrawable
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

    private val breatheInTime : Long = 2000
    private val holdTime : Long = 3500
    private val breatheOutTime : Long = 4000

    private lateinit var isabelleAnim : AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.breathe_main)

        isabelleBreatheIV.apply {
            setBackgroundResource(R.drawable.isabelle_anim)
            isabelleAnim = background as AnimationDrawable
        }
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
                isabelleAnim.start()
                breatheIn()
            }
        }.start()

    }

    fun breatheIn() {
        breatheTV.text = "Breathe in through your nose"

        var breatheTimer = object : CountDownTimer(breatheInTime, 10) {

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

        var breatheTimer = object : CountDownTimer(holdTime, 1000) {

            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                breatheOut()
            }
        }.start()
    }

    fun breatheOut() {
        breatheTV.text = "Breathe out through your mouth"

        var breatheTimer = object : CountDownTimer(breatheOutTime, 10) {

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