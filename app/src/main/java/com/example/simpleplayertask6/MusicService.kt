package com.example.simpleplayertask6

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.example.exoplayermyui.data.TrackRepository
import com.example.exoplayermyui.data.TrackResource
import com.example.exoplayermyui.model.TrackJson
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.media.browse.MediaBrowser.MediaItem.FLAG_PLAYABLE as FLAG_PL

class MusicService : MediaBrowserServiceCompat() {

    private var mediaSession: MediaSessionCompat? = null
    private var musicCatalog: List<TrackJson>? = null
    private var repository: TrackRepository? = null
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var exoPlayer: SimpleExoPlayer? = null

    init {
        serviceScope.launch {
            musicCatalog = repository?.getTracks()
        }
    }

    @SuppressLint("WrongConstant")
    override fun onCreate() {
        super.onCreate()
        Log.d("DEBUG", "SERVICE CREATE")
        repository = TrackRepository(TrackResource(), this.applicationContext)
        mediaSession = MediaSessionCompat(this, "MusicService")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "NOTIFICATION_CHANNEL_ID",
                "Player controls",
                NotificationManagerCompat.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        mediaSession?.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                    or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )

        val activityIntent = Intent(applicationContext, MainActivity::class.java)
        mediaSession?.setSessionActivity(
            PendingIntent.getActivity(
                applicationContext, 0,
                activityIntent, 0
            )
        )

        val mediaButtonIntent = Intent(
            Intent.ACTION_MEDIA_BUTTON, null, applicationContext,
            MediaButtonReceiver::class.java
        )
        mediaSession?.setMediaButtonReceiver(
            PendingIntent.getBroadcast(
                applicationContext, 0,
                mediaButtonIntent, 0
            )
        )

        val notification = getNotification(PlaybackStateCompat.STATE_PLAYING)
        NotificationManagerCompat.from(this@MusicService).notify(
            33,
            getNotification(PlaybackStateCompat.STATE_PLAYING)
        )
    }

    private fun refreshNotificationAndForegroundStatus(playbackState: Int) {
        when (playbackState) {
            PlaybackStateCompat.STATE_PLAYING -> startForeground(
                33,
                getNotification(playbackState)
            )
            PlaybackStateCompat.STATE_PAUSED -> {
                NotificationManagerCompat.from(this@MusicService).notify(
                    33,
                    getNotification(playbackState)
                )
                stopForeground(false)
            }
            else -> stopForeground(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot("Root", null)
    }

    @SuppressLint("WrongConstant")
    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val data = musicCatalog?.let { ArrayList<MediaBrowserCompat.MediaItem>(it?.size) }
        val descriptionBuilder = MediaDescriptionCompat.Builder()

        for ((i, track) in musicCatalog!!.withIndex()) {
            Log.i("TAG", "track = ${track.title}")
            // val track = musicCatalog.getTrackByIndex(i)
            val description = descriptionBuilder
                .setDescription(track.artist)
                .setTitle(track.title)
                .setSubtitle(track.artist)
                .setIconUri(Uri.parse(track.bitmapUri))
                .setMediaId(i.toString())
                .build()
            data?.add(MediaBrowserCompat.MediaItem(description, FLAG_PL))
        }
        result.sendResult(data)
    }

    inner class MediaServiceBinder : Binder() {
        fun getMediaSessionToken() = mediaSession?.sessionToken
    }

    private fun getNotification(playbackState: Int): Notification {
        // Get the session's metadata
        val controller = mediaSession?.controller
        val mediaMetadata = controller?.metadata
        val description = mediaMetadata?.description
        val builder = NotificationCompat.Builder(this, "channelId111")
        builder.addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_media_previous,
                "previous",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
            )
        )

        if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
            builder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_pause,
                    "pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_PAUSE
                    )
                )
            )
        } else {
            builder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_play,
                    "play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_PLAY
                    )
                )
            )
        }

        builder.addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_media_next,
                "next",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                )
            )
        )

        return builder.build()
    }
}
