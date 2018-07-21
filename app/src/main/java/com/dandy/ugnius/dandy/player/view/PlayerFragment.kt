package com.dandy.ugnius.dandy.player.view

import android.content.res.ColorStateList
import android.graphics.Color.WHITE
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.graphics.drawable.DrawableCompat
import com.App
import android.support.v4.view.ViewCompat
import android.support.v7.graphics.Palette
import android.view.*
import android.widget.SeekBar
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.main.MainActivity
import com.dandy.ugnius.dandy.player.presenter.PlayerPresenter
import com.spotify.sdk.android.player.*
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.playback_controls.*
import kotlinx.android.synthetic.main.view_player.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class PlayerFragment : Fragment(), PlayerView {

    @Inject lateinit var player: SpotifyPlayer
    @Inject lateinit var notificationBuilder: NotificationCompat.Builder
    private val playerPresenter by lazy { PlayerPresenter(this).also { it.player = player } }
    private val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
    private var isTracking = false

    override fun updateProgress() {
        if (!isTracking) {
            seekbar.progress += 1
            timeElapsed?.text = formatter.format(seekbar.progress * 1000)
        }
    }

    override fun toggleShuffle(isShuffle: Boolean) {
        if (isShuffle) {
            shuffle.upscale()
        } else {
            shuffle.downscale()
        }
    }

    override fun toggleReplay(isReplay: Boolean) {
        if (isReplay) {
            replay.upscale()
        } else {
            replay.downscale()
        }
    }

    override fun shouldRewind() = seekbar?.progress ?: 0 > 10

    override fun hasTrackEnded() = seekbar?.progress == seekbar?.max

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.requestApplyInsets(root)
        (activity?.applicationContext as App).mainComponent?.inject(this)
        savedInstanceState?.let { playerPresenter.setState(it) }
        initializeViews()
        arguments?.let { playerPresenter.setState(it) }
        playerPresenter.playTrack()

    }

    override fun togglePlayButton(isPaused: Boolean) {
        if (isPaused) {
            playOrPause.setImageResource(R.drawable.play)
        } else {
            playOrPause.setImageResource(R.drawable.pause)
        }
    }

    private fun initializeViews() {
        trackTitle.isSelected = true
        trackArtist.isSelected = true
        seekbar.setPadding(0, 0, 0, 0)
        previous.setOnClickListener { playerPresenter.skipToPrevious() }
        replay.setOnClickListener { playerPresenter.toggleReplay() }
        next.setOnClickListener { playerPresenter.skipToNext() }
        playOrPause.setOnClickListener { playerPresenter.togglePlayback() }
        shuffle.setOnClickListener { playerPresenter.toggleShuffle() }
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isTracking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                isTracking = false
                playerPresenter.seekToPosition(seekBar.progress)
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        with(activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.expand)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        (activity as MainActivity).menuInflater.inflate(R.menu.player_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.actionMore) {
        } else {
            activity?.supportFragmentManager?.popBackStack()
        }
        return true
    }

    override fun update(track: Track) {
        with(track) {
            trackTitle?.text = name
            trackArtist?.text = artists
            artwork?.loadBitmap(images.first(), context!!) {
                it.extractSwatch().subscribeBy(
                        onSuccess = { setAccentColor(it) },
                        onComplete = { playbackControls?.background = ColorDrawable(WHITE) }
                    )
            }
        }
        seekbar.progress = 0
        duration.text = track.duration
        seekbar.max = Utilities.durationToSeconds(track.duration)
    }

    private fun setAccentColor(swatch: Palette.Swatch) {
        val blendedColor = Utilities.whiteBlend(context!!, swatch.rgb, 0.3F)
        val adjustedColor = Drawables.lightenOrDarken(swatch.rgb, 0.3)
        DrawableCompat.setTint(replay.drawable, adjustedColor)
        DrawableCompat.setTint(shuffle.drawable, adjustedColor)
        seekbar.progressTintList = ColorStateList.valueOf(adjustedColor)
        seekbar.progressBackgroundTintList = ColorStateList.valueOf(blendedColor)
        seekbar.thumbTintList = ColorStateList.valueOf(adjustedColor)
        playbackControls.shade(color = blendedColor, ratio = 0.5F)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        playerPresenter.saveState(outState)
    }


//    private fun showNotification(artwork: Bitmap, track: Track) {
//
//        val intent = Intent(activity, LoginActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//
//        val pendingIntent = PendingIntent.getActivity(activity, 0, intent, 0)
//        val previousIntent = Intent(NOTIFICATION_ACTION_PREVIOUS)
//        val nextIntent = Intent(NOTIFICATION_ACTION_NEXT)
//
//        val startIntent = Intent(NOTIFICATION_ACTION_PLAY)
//        val pauseIntent = Intent(NOTIFICATION_ACTION_PAUSE)
//
//        val previousPendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_REQUEST_CODE, previousIntent, 0)
//        val nextPendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_REQUEST_CODE, nextIntent, 0)
//        val startPreviousIntent = PendingIntent.getBroadcast(context, NOTIFICATION_REQUEST_CODE, startIntent, 0)
//        val pausePendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_REQUEST_CODE, pauseIntent, 0)
//
//        val title = track.name
//        val artists = track.artists
//
//        this.notificationBuilder
//            .setSmallIcon(R.mipmap.ic_launcher_round)
//            .setContentTitle(title)
//            .setContentText(artists)
//            .setPriority(PRIORITY_LOW)
//            .setShowWhen(false)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .addAction(R.drawable.ic_skip_previous_black_24dp, "Previous", previousPendingIntent) // #0
//            .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)  // #1
//            .addAction(R.drawable.ic_skip_next_black_24dp, "Next", nextPendingIntent)
//            .setLargeIcon(artwork)
//            .setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle()
//                .setShowActionsInCompactView(0, 1, 2)
//                .setMediaSession(MediaSessionCompat(activity!!, "media session").sessionToken))
//        NotificationManagerCompat.from(activity!!).notify(1, notificationBuilder.build())
//
//    }

}
