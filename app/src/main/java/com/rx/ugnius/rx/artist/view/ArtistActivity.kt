package com.rx.ugnius.rx.artist.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.*
import android.graphics.drawable.shapes.RectShape
import android.support.v7.widget.RecyclerView.VERTICAL
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.rx.ugnius.rx.R
import com.rx.ugnius.rx.artist.common.adjustColorLightness
import com.rx.ugnius.rx.artist.presenter.ArtistPresenter
import com.rx.ugnius.rx.artist.model.entities.Album
import com.rx.ugnius.rx.artist.model.entities.Artist
import com.rx.ugnius.rx.artist.model.entities.Track
import com.rx.ugnius.rx.artist.common.extractDominantSwatch
import com.rx.ugnius.rx.artist.view.adapters.AlbumsAdapter
import com.rx.ugnius.rx.artist.view.adapters.SimilarArtistsAdapter
import com.rx.ugnius.rx.artist.view.adapters.TracksAdapter
import com.rx.ugnius.rx.artist.view.decorations.VerticalGridDecorator
import com.rx.ugnius.rx.artist.view.utility.ViewUtils
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.view_artist.*
import kotlinx.android.synthetic.main.artist_recycler.view.*
import kotlin.collections.ArrayList

class ArtistActivity : AppCompatActivity(), View {

    private companion object {
        const val ARTIST_PAGER_ENTRIES_COUNT = 3
    }

    private val tracksAdapter by lazy { TracksAdapter(this) }
    private val albumsAdapter by lazy { AlbumsAdapter(this) }
    private val similarArtistsAdapter by lazy { SimilarArtistsAdapter(this) }
    private val presenter = ArtistPresenter(this)
    private var albumsSingle: Single<List<Track>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_artist)
        artistPager.offscreenPageLimit = ARTIST_PAGER_ENTRIES_COUNT
        artistPager.adapter = ArtistPagerAdapter(this)
        artistPagerTabs.setupWithViewPager(artistPager)
        presenter.queryArtist("5IcR3N7QB1j6KBL8eImZ8m")
        presenter.queryTopTracks("5IcR3N7QB1j6KBL8eImZ8m", "ES")
        albumsSingle = presenter.getAlbumsObservable("5IcR3N7QB1j6KBL8eImZ8m")
        albumsSingle?.subscribe()
        presenter.querySimilarArtists("5IcR3N7QB1j6KBL8eImZ8m")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.artist_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.actionFavorite) {
            println("favorite was clicked")
            true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    override fun displayArtistInfo(artist: Artist) {
        runOnUiThread {
            //todo paziureti kodel cia meto warningus
            val monthlyListeners = String.format(getString(R.string.monthly_listeners, artist.followers.total))
            listeners.text = monthlyListeners
            collapsingLayout.title = artist.name
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
                                                collapsingLayout.setContentScrimColor(adjustedColor)
                                                collapsingLayout.setStatusBarScrimColor(adjustedColor)
//                                                blurredLayout.setBackgroundColor(ColorUtils.setAlphaComponent(adjustedColor, 70))
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
                    view.artistRecycler.layoutManager = LinearLayoutManager(this@ArtistActivity)
                    view.artistRecycler.adapter = tracksAdapter
                }
                1 -> {
                    val columnCount = ViewUtils.getNumberOfColumns(context = this@ArtistActivity, columnWidth = 200)
                    val decoration = VerticalGridDecorator(ViewUtils.dpToPx(this@ArtistActivity, 16), columnCount)
                    view.artistRecycler.addItemDecoration(decoration)
                    view.artistRecycler.layoutManager = GridLayoutManager(this@ArtistActivity, columnCount, VERTICAL, false)
                    view.artistRecycler.adapter = albumsAdapter
//                    view.unfoldAlbums.visibility = android.view.View.VISIBLE
//                    view.unfoldAlbums.setOnClickListener {
//                        albumsSingle?.subscribeBy(onSuccess = { tracks ->
//                            view.artistRecycler.layoutManager = LinearLayoutManager(this@ArtistActivity)
//                            view.artistRecycler.adapter = tracksAdapter
//                            tracksAdapter.entries = ArrayList(tracks)
//                        })
//                    }
                }
                2 -> {
                    //TODO padaryti normalu interfaca o ne View
                    view.unfoldAlbums.visibility = android.view.View.GONE
                    view.artistRecycler.layoutManager = GridLayoutManager(this@ArtistActivity, 3, VERTICAL, false)
                    view.artistRecycler.adapter = similarArtistsAdapter
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

    override fun displaySimilarArtists(artists: List<Artist>) {
        similarArtistsAdapter.entries = ArrayList(artists)
    }
}
