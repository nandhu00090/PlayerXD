package com.example.drmplayer

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import androidx.media3.exoplayer.drm.LocalMediaDrmCallback
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView

@UnstableApi
class MainActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var inputLayout: LinearLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        playerView = findViewById(R.id.player_view)
        inputLayout = findViewById(R.id.input_layout)
        
        val etMpdUrl = findViewById<EditText>(R.id.et_mpd_url)
        val etKeys = findViewById<EditText>(R.id.et_keys)
        val btnPlay = findViewById<Button>(R.id.btn_play)

        btnPlay.setOnClickListener {
            val mpdUrl = etMpdUrl.text.toString().trim()
            val keysJson = etKeys.text.toString().trim()

            if (mpdUrl.isNotEmpty() && keysJson.isNotEmpty()) {
                // Play button amukunathum text box-a hide pannidum
                inputLayout.visibility = View.GONE
                startPlayer(mpdUrl, keysJson)
            } else {
                Toast.makeText(this, "Link and Keys rendaiyum paste pannunga!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startPlayer(mpdUrl: String, keysJson: String) {
        // Pazhaiya video oditu iruntha atha stop panrathukku
        player?.release()

        try {
            val clearKeyBytes = keysJson.toByteArray(Charsets.UTF_8)
            val drmCallback = LocalMediaDrmCallback(clearKeyBytes)
            val drmSessionManager = DefaultDrmSessionManager.Builder()
                .setUuidAndExoMediaDrmProvider(C.CLEARKEY_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER)
                .build(drmCallback)

            val mediaSourceFactory = DefaultMediaSourceFactory(this)
                .setDrmSessionManagerProvider { drmSessionManager }

            player = ExoPlayer.Builder(this)
                .setMediaSourceFactory(mediaSourceFactory)
                .build()

            playerView.player = player

            val mediaItem = MediaItem.Builder()
                .setUri(mpdUrl)
                .setDrmConfiguration(MediaItem.DrmConfiguration.Builder(C.CLEARKEY_UUID).build())
                .build()

            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.playWhenReady = true
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            inputLayout.visibility = View.VISIBLE
        }
    }
    
    // Back button amukuna thirumba text box UI-ku vara
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (inputLayout.visibility == View.GONE) {
            player?.stop()
            inputLayout.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}

