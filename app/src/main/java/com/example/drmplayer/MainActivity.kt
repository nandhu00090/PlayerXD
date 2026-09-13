package com.example.drmplayer

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val playerView = findViewById<PlayerView>(R.id.player_view)
        
        // Amazon mpd link from your screenshot
        val mpdUrl = "https://a189vod-dash-pv-ta-amazon.akamaized.net/iad_2/55c9/8684/3480/4811-ba41-2a1da7c44d94/382e4006-0ebc-4ecb-9614-de4e62ba290b_corrected.mpd"
        
        // Hex to Base64Url converted keys (Dummy Base64 added, you need real conversion)
        val clearKeyJson = """{"keys":[{"kty":"oct","k":"bV8gsMJVQweLj6I2ZTr5nw","kid":"bV8gsMJVQweLj6I2ZTr5nw"}],"type":"temporary"}"""
        val clearKeyBytes = clearKeyJson.toByteArray(Charsets.UTF_8)

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
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
