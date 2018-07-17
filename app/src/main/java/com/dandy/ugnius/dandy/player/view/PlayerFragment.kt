package com.dandy.ugnius.dandy.player.view

import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import com.App.Companion.NOTIFICATION_ACTION_PREVIOUS
import com.App.Companion.NOTIFICATION_ACTION_PLAY
import com.App.Companion.NOTIFICATION_ACTION_PAUSE
import com.App.Companion.NOTIFICATION_ACTION_NEXT
import com.App.Companion.NOTIFICATION_REQUEST_CODE
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.App
import android.support.v4.app.NotificationCompat.PRIORITY_LOW
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v4.view.ViewCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.Utilities
import com.dandy.ugnius.dandy.artist.common.adjustColorLuminance
import com.dandy.ugnius.dandy.artist.common.extractDominantSwatch
import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.login.view.LoginActivity
import com.dandy.ugnius.dandy.main.MainActivity
import com.dandy.ugnius.dandy.player.presenter.PlayerPresenter
import com.github.florent37.viewanimator.ViewAnimator
import com.spotify.sdk.android.player.*
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.playback_controls.*
import kotlinx.android.synthetic.main.view_player.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class PlayerFragment : Fragment(), PlayerView {

    @Inject
    lateinit var player: SpotifyPlayer
    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder
    @Inject
    lateinit var preferences: SharedPreferences
    private val playerPresenter by lazy { PlayerPresenter(this).also { it.player = player } }
    private val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
    private var seekBar: SeekBar? = null
    private var artwork: ImageView? = null
    private var trackTitle: TextView? = null
    private var trackArtist: TextView? = null
    private var duration: TextView? = null
    private var timeElapsed: TextView? = null
    private var previous: ImageView? = null
    private var shuffle: ImageView? = null
    private var next: ImageView? = null
    private var root: RelativeLayout? = null
    private var playbackControls: ConstraintLayout? = null
    private var toolbar: Toolbar? = null

    override fun hasTrackEnded() = seekBar?.progress == seekBar?.max

    override fun updateProgress() {
        seekbar?.let {
            it.progress += 1
            timeElapsed?.text = formatter.format(it.progress * 1000)
        }
    }

    override fun toggleShuffle(shuffle: Boolean) {
        if (shuffle) {
            ViewAnimator.animate(this.shuffle)
                .scale(1F, 1.5F, 2F)
                .start()
        } else {
            ViewAnimator.animate(this.shuffle)
                .scale(2F, 1.5F, 1F)
                .start()
        }
    }

    override fun shouldRewind() = seekBar?.progress ?: 0 > 10

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity?.applicationContext as App).mainComponent?.inject(this)
        playerPresenter.setState(savedInstanceState)
        with(view) {
            seekBar = findViewById(R.id.seekbar)
            artwork = findViewById(R.id.artwork)
            trackTitle = findViewById(R.id.trackTitle)
            trackArtist = findViewById(R.id.artistTitle)
            duration = findViewById(R.id.duration)
            timeElapsed = findViewById(R.id.timeElapsed)
            previous = findViewById(R.id.previous)
            next = findViewById(R.id.next)
            shuffle = findViewById(R.id.shuffle)
            root = findViewById(R.id.root)
            playbackControls = findViewById(R.id.playbackControls)
        }
        ViewCompat.requestApplyInsets(root)
        initializeViews()
        playerPresenter.setState(arguments)
        playerPresenter.playTrack()


    }

    override fun updatePlayButton(isPaused: Boolean) {
        if (isPaused) {
            playOrPause.setImageResource(R.drawable.play)
        } else {
            playOrPause.setImageResource(R.drawable.pause)
        }
    }

    private fun initializeViews() {
        trackTitle?.isSelected = true
        trackArtist?.isSelected = true
        seekBar?.setPadding(0, 0, 0, 0)
        previous?.setOnClickListener { playerPresenter.skipToPrevious() }
        next?.setOnClickListener { playerPresenter.skipToNext() }
        playOrPause.setOnClickListener { playerPresenter.togglePlayback() }
        shuffle?.setOnClickListener { playerPresenter.toggleShuffle() }
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
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
            updateArtwork(this)
        }
        seekBar?.progress = 0
        duration?.text = track.duration
        seekBar?.max = Utilities.durationToSeconds(track.duration)

    }

    private fun updateArtwork(track: Track) {
        artwork?.post {
            Glide.with(context ?: return@post)
                .asBitmap()
                .load(track.images.first())
                .into(object : SimpleTarget<Bitmap>(artwork!!.width, artwork!!.height) {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        artwork?.setImageBitmap(resource)
                        resource.extractDominantSwatch()
                            .subscribeBy(
                                onSuccess = { setAccentColor(it) },
                                onComplete = { playbackControls?.background = ColorDrawable(Color.WHITE) }
                            )
//                    track.let { showNotification(resource, it) }
                    }
                })
        }
    }

    //todo iskelti situos
    private fun setAccentColor(swatch: Palette.Swatch) {
        val transparentWhite = ContextCompat.getColor(context!!, R.color.opaqueWhite)
        val blendedColor = ColorUtils.blendARGB(transparentWhite, swatch.rgb, 0.1F)

        seekBar?.progressTintList = ColorStateList.valueOf(swatch.rgb)
        seekBar?.progressBackgroundTintList = ColorStateList.valueOf(blendedColor)
        seekBar?.thumbTintList = ColorStateList.valueOf(swatch.rgb)

        playbackControls?.let { createShader(it, blendedColor) }
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
                    floatArrayOf(0f, 0.5f, 1f),
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
