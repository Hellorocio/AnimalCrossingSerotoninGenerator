package com.example.animalcrossingserotoningenerator

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Repeat
import kotlinx.coroutines.*

import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
import kotlinx.android.synthetic.main.music_main.*
import java.util.concurrent.atomic.AtomicBoolean

class MusicActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val clientId = "fc350938cc614782b044ff892d8b5498"
    private val redirectUri = "http://com.example.animalcrossingserotoningenerator/callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val clientSecret = "04ab4d2f514241f6a528bb6e797d76d6"
    private var songLength = 0
    private var paused = false
    private var isPlaying: AtomicBoolean = AtomicBoolean()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.music_main)
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "ONSTART!")
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        Log.d("MainActivity", "BUILD THINGY!")

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })

    }

    private fun connected() {
        spotifyAppRemote?.let {
            // Play a playlist
            val playlistURI = "spotify:playlist:0h3oRfCRnvFkbIJZGsfY82"
            it.playerApi.play(playlistURI)
            it.playerApi.setShuffle(true)
            it.playerApi.setRepeat(Repeat.ALL)
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {pState: PlayerState ->
                val track: Track = pState.track
                songNameTV.text = track.name
                Log.d("MainActivity", track.name + " by " + track.artist.name)
                isPlaying.set(true)
                songLength = track.duration.toInt()
                seek.max = songLength
                seek.min = 0
                var result = it.imagesApi.getImage(track.imageUri)
                result.setResultCallback{ pic: Bitmap ->
                    songPictureIV.setImageBitmap(pic)
                }

            }

            playIV.setOnClickListener{_: View ->
                if(paused){
                    playIV.setImageResource(R.drawable.ic_pause_black_24dp)
                    it.playerApi.resume()
                } else {
                    playIV.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                    it.playerApi.pause()

                }
                paused = !paused
            }

            forwardIV.setOnClickListener{_: View ->
                it.playerApi.skipNext()
            }

            backIV.setOnClickListener{_: View ->
                it.playerApi.skipPrevious()
            }

            seek.setOnSeekBarChangeListener (object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged (seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser)
                    {
                        it.playerApi.seekTo(progress.toLong())
                    }
                }

                override fun onStartTrackingTouch (seekBar: SeekBar) {}

                override fun onStopTrackingTouch (seekBar: SeekBar) {}
            })

            val millisec = 100L
            launch {
                displayTime(millisec)
            }
        }

    }

    // This method converts time in milliseconds to minutes-second formated string
    private fun convertTime(milliseconds: Int): String {
        val minutes = (milliseconds.toDouble() / 1000.0 / 60.0).toInt()
        val millisecondsRemaining = milliseconds - minutes * 60 * 1000
        val seconds = millisecondsRemaining / 1000

        return String.format("%02d:%02d", minutes, seconds)
    }

    // The suspend modifier marks this as a function called from a coroutine
    // Note, this whole function is somewhat reminiscent of the Timer class
    // from Fling and Peck.  We use an independent thread to manage one small
    // piece of our GUI.  But beware, if this coroutine accesses any data
    // that is also accessed by the main thread, then it should be an Atomic
    // type, like AtomicBoolean
    private suspend fun displayTime(misc: Long) {
        // While the coroutine is running and has not been canceled by its parent
        while (coroutineContext.isActive) {
            // XXX Write me
            if (isPlaying.get() && spotifyAppRemote != null)
            {

                val result = spotifyAppRemote!!.playerApi.playerState
                result.setResultCallback {
                    startTimeTV.text = convertTime(it.playbackPosition.toInt())
                    endTimeTV.text = convertTime(songLength - it.playbackPosition.toInt())
                    seek.progress = it.playbackPosition.toInt()
                }
            }
            // Leave this code as is.  it inserts a delay so that this thread does
            // not consume too much CPU
            delay(misc)
        }
    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            it.playerApi.pause()
            it.playerApi.seekTo(0)
            SpotifyAppRemote.disconnect(it)
        }

    }
}