package com.dandy.ugnius.dandy.player.view

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.support.v4.app.Fragment
import com.App.Companion.NOTIFICATION_ACTION_PREVIOUS
import com.App.Companion.NOTIFICATION_ACTION_PLAY
import com.App.Companion.NOTIFICATION_ACTION_PAUSE
import com.App.Companion.NOTIFICATION_ACTION_NEXT
import android.graphics.PorterDuff.Mode.MULTIPLY
import com.App.Companion.NOTIFICATION_REQUEST_CODE
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.App
import android.support.v4.app.NotificationCompat.PRIORITY_LOW
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v7.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.artist.common.adjustColorLightness
import com.dandy.ugnius.dandy.artist.common.extractDominantSwatch
import com.dandy.ugnius.dandy.artist.common.random
import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.login.view.LoginActivity
import com.dandy.ugnius.dandy.model.clients.APIClient
import com.dandy.ugnius.dandy.player.presenter.PlayerPresenter
import com.github.florent37.viewanimator.ViewAnimator
import com.spotify.sdk.android.player.*
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.view_player.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


class PlayerFragment : Fragment(), PlayerView {

    @Inject lateinit var player: SpotifyPlayer
    @Inject lateinit var notificationBuilder: NotificationCompat.Builder
    @Inject lateinit var preferences: SharedPreferences
    private val playerPresenter by lazy { PlayerPresenter(this).also { it.player = player } }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.applicationContext as App).mainComponent?.inject(this)
        playerPresenter.setState(savedInstanceState)
        return inflater.inflate(R.layout.view_player, container, false)
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        seekbar.setPadding(0, 0, 0, 0)
        arguments?.putBoolean("shuffle", preferences.getBoolean("shuffle", false))
        playerPresenter.setState(arguments)
        view?.post { playerPresenter.playTrack() }
    }

    override fun update(track: Track) {
        with(track) {
            trackTitle.text = name
            artistTitle.text = artists
            updateArtwork(this)
        }
    }

    private fun updateArtwork(track: Track) {
        Glide.with(context ?: return)
            .asBitmap()
            .load(track.images.first())
            .into(object : SimpleTarget<Bitmap>(artwork.width, artwork.height) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    artwork?.setImageBitmap(resource)
                    resource.extractDominantSwatch()
                        .subscribeBy(
                            onSuccess = { setAccentColor(it) },
                            onComplete = { playbackControls.background = ColorDrawable(Color.WHITE) }
                        )
//                    track.let { showNotification(resource, it) }
                }
            })
    }

    private fun setListeners() {
        previous.setOnClickListener { playerPresenter.skipToPrevious() }
        next.setOnClickListener { playerPresenter.skipToNext() }
        play.setOnClickListener { playerPresenter.pause() }
        shuffle.setOnClickListener {
            ViewAnimator.animate(shuffle)
                .scale(0F, 0.5F)
                .start()
            preferences.edit().putBoolean("shuffle", true).apply()
            playerPresenter.toggleShuffle()
        }
    }

    //todo iskelti situos
    private fun setAccentColor(swatch: Palette.Swatch) {
        val transparentWhite = ContextCompat.getColor(context!!, R.color.opaqueWhite)
        val blendedColor = ColorUtils.blendARGB(transparentWhite, swatch.rgb, 0.1F)
        val adjustedColor = adjustColorLightness(color = swatch.rgb, lightness = 0.2F)
        seekbar.progressDrawable.setColorFilter(adjustedColor, MULTIPLY);
        seekbar.thumb.setColorFilter(adjustedColor, MULTIPLY)
        createShader(playbackControls, blendedColor)
    }

    private fun createShader(view: android.view.View, color: Int) {
        val shader = object : ShapeDrawable.ShaderFactory() {
            override fun resize(width: Int, height: Int): Shader {
                return LinearGradient(
                    (view.width / 2).toFloat(),
                    0F,
                    (width / 2).toFloat(),
                    (view.height).toFloat(),
                    intArrayOf(color, Color.WHITE, Color.WHITE),
                    floatArrayOf(0f, 0.3f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
        }
        with(PaintDrawable()) {
            shape = RectShape()
            shaderFactory = shader
            view.background = this
        }
    }

    private fun showNotification(artwork: Bitmap, track: Track) {

        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val pendingIntent = PendingIntent.getActivity(activity, 0, intent, 0)
        val previousIntent = Intent(NOTIFICATION_ACTION_PREVIOUS)
        val nextIntent = Intent(NOTIFICATION_ACTION_NEXT)

//        tracks?.elementAt(3)?.let {
//            nextIntent.putExtra("nextTrack", it)
//        }
//        nextIntent.extras?.getParcelable<Track>("nextTrack")

        val startIntent = Intent(NOTIFICATION_ACTION_PLAY)
        val pauseIntent = Intent(NOTIFICATION_ACTION_PAUSE)

        val previousPendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_REQUEST_CODE, previousIntent, 0)
        val nextPendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_REQUEST_CODE, nextIntent, 0)
        val startPreviousIntent = PendingIntent.getBroadcast(context, NOTIFICATION_REQUEST_CODE, startIntent, 0)
        val pausePendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_REQUEST_CODE, pauseIntent, 0)

        val title = track.name
        val artists = track.artists

        this.notificationBuilder
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(artists)
            .setPriority(PRIORITY_LOW)
            .setShowWhen(false)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_skip_previous_black_24dp, "Previous", previousPendingIntent) // #0
            .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)  // #1
            .addAction(R.drawable.ic_skip_next_black_24dp, "Next", nextPendingIntent)
            .setLargeIcon(artwork)
            .setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(MediaSessionCompat(activity!!, "media session").sessionToken))
        NotificationManagerCompat.from(activity!!).notify(1, notificationBuilder.build())

    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        playerPresenter.saveState(outState)
    }


}
