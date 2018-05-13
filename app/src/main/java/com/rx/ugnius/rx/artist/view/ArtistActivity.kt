package com.rx.ugnius.rx.artist.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color.WHITE
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.*
import android.graphics.drawable.shapes.RectShape
import android.os.Handler
import android.support.v7.widget.RecyclerView.VERTICAL
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.rx.ugnius.rx.R
import com.rx.ugnius.rx.adjustColorLightness
import com.rx.ugnius.rx.artist.ArtistPresenter
import com.rx.ugnius.rx.artist.model.entities.Album
import com.rx.ugnius.rx.artist.model.entities.Artist
import com.rx.ugnius.rx.artist.model.entities.Track
import com.rx.ugnius.rx.extractDominantSwatch
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.view_artist.*
import kotlinx.android.synthetic.main.artist_header.*
import kotlinx.android.synthetic.main.artist_recycler.view.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class ArtistActivity : AppCompatActivity(), View {

    private companion object {
        const val ARTIST_PAGER_ENTRIES_COUNT = 2
    }

    private val tracksAdapter by lazy { TracksAdapter(this) }
    private val albumsAdapter by lazy { AlbumsAdapter(this) }
    private val linearLayoutManager by lazy { LinearLayoutManager(this) }
    private val gridLayoutManager by lazy { GridLayoutManager(this, 2, VERTICAL, false) }
    private val presenter = ArtistPresenter(this)
    private var albumsSingle: Single<List<Track>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_artist)
        artistPager.offscreenPageLimit = ARTIST_PAGER_ENTRIES_COUNT
        artistPager.adapter = ArtistPagerAdapter(this)
        artistPagerTabs.setupWithViewPager(artistPager)
        presenter.queryArtist("6rYogEVj60BCIsLukpAnwr")
        presenter.queryTopTracks("6rYogEVj60BCIsLukpAnwr", "ES")
        albumsSingle = presenter.getAlbumsOberservable("6rYogEVj60BCIsLukpAnwr")
        presenter.querySimilarArtists("6rYogEVj60BCIsLukpAnwr")
        albumsSingle?.subscribe()
    }

    override fun displayArtistInfo(artist: Artist) {
        runOnUiThread {
            //todo paziureti kodel cia meto warningus
            val monthlyListeners = String.format(getString(R.string.monthly_listeners, artist.followers.total))
            listeners.text = monthlyListeners
            name.text = artist.name
            Glide.with(applicationContext)
                    .asBitmap()
                    .load(artist.images.first().url)
                    .into(object : SimpleTarget<Bitmap>(background.width, background.height) {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            background.setImageBitmap(resource)
                            resource.extractDominantSwatch()
                                    .subscribeBy(
                                            onSuccess = {
                                                val adjustedColor = adjustColorLightness(color = it.rgb, lightness = 0.3F)
                                                val transparentWhite = ContextCompat.getColor(applicationContext, R.color.transparentWhite)
                                                val blendedColor = ColorUtils.blendARGB(transparentWhite, it.rgb, 0.5F)
                                                createShader(artistPager, blendedColor)
                                                artistPagerTabs.setSelectedTabIndicatorColor(adjustedColor);
                                                artistPagerTabs.setTabTextColors(adjustedColor, adjustedColor)
                                                blurredLayout.setBackgroundColor(ColorUtils.setAlphaComponent(adjustedColor, 70))
                                            },
                                            onComplete = { artistPager.background = ColorDrawable(Color.WHITE) }
                                    )

                        }
                    })
        }
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
                        floatArrayOf(0F, 0.2f, 1f),
                        Shader.TileMode.CLAMP
                )
            }
        }

        val paintDrawable = PaintDrawable()
        paintDrawable.shape = RectShape()
        paintDrawable.shaderFactory = shader
        view.setBackground(paintDrawable);
    }

    override fun displayArtistTracks(tracks: ArrayList<Track>) {
        runOnUiThread {
            tracksAdapter.entries = tracks
        }
    }

    override fun displayArtistAlbums(albums: List<Album>) {
        albumsAdapter.entries = albums
    }

    override fun showError(message: String) {
        println("show lottie error ")
    }

    private inner class ArtistPagerAdapter(context: Context) : PagerAdapter() {

        private val inflater = LayoutInflater.from(context)


        override fun getCount() = ARTIST_PAGER_ENTRIES_COUNT

        override fun isViewFromObject(view: android.view.View, `object`: Any) = view == `object`


        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = inflater.inflate(R.layout.artist_recycler, container, false) as LinearLayout
            when (position) {
                0 -> {
                    view.artistRecycler.layoutManager = linearLayoutManager
                    view.artistRecycler.adapter = tracksAdapter
                }
                1 -> {
                    view.artistRecycler.layoutManager = gridLayoutManager
                    view.artistRecycler.adapter = albumsAdapter
                    view.unfoldAlbums.visibility = android.view.View.VISIBLE
                    view.unfoldAlbums.setOnClickListener {
                        albumsSingle?.subscribeBy(onSuccess = { tracks ->
                            view.artistRecycler.layoutManager = LinearLayoutManager(this@ArtistActivity)
                            view.artistRecycler.adapter = tracksAdapter
                            tracksAdapter.entries = ArrayList(tracks)
                        })
                    }
                }
            }
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as android.view.View)
        }

        override fun getPageTitle(position: Int) = when (position) {
            0 -> "Songs"
            1 -> "Albums"
            2 -> "Similar"
            else -> throw IllegalArgumentException("Invalid item count was specified")
        }
    }
}
