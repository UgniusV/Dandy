package com.dandy.ugnius.dandy.artist.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color.WHITE
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v4.view.PagerAdapter
import android.support.v7.graphics.Palette
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dandy.ugnius.dandy.artist.common.adjustColorLightness
import com.dandy.ugnius.dandy.artist.common.extractDominantSwatch
import com.dandy.ugnius.dandy.artist.common.getNumberOfColumns
import com.dandy.ugnius.dandy.artist.model.entities.Album
import com.dandy.ugnius.dandy.artist.model.entities.Artist
import com.dandy.ugnius.dandy.artist.model.entities.Track
import com.dandy.ugnius.dandy.artist.presenter.ArtistPresenter
import android.support.v7.widget.RecyclerView.VERTICAL
import android.widget.LinearLayout
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.artist.view.decorations.VerticalGridDecorator
import com.dandy.ugnius.dandy.artist.di.ArtistModule
import com.dandy.ugnius.dandy.artist.di.DaggerArtistComponent
import com.dandy.ugnius.dandy.artist.view.adapters.AlbumsAdapter
import com.dandy.ugnius.dandy.artist.view.adapters.SimilarArtistsAdapter
import com.dandy.ugnius.dandy.artist.view.adapters.TracksAdapter
import com.dandy.ugnius.dandy.artist.view.interfaces.ArtistFragmentDelegate
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.artist_recycler.view.*
import kotlinx.android.synthetic.main.view_artist.*
import com.dandy.ugnius.dandy.main.MainActivity
import javax.inject.Inject

class ArtistFragment : Fragment(), ArtistView {


    private companion object {
        const val ARTIST_PAGER_ENTRIES_COUNT = 3
    }

    @Inject lateinit var presenter: ArtistPresenter
    private val tracksAdapter by lazy { TracksAdapter(context!!, { trackId -> delegate?.onArtistTrackClicked(trackId) }) }
    private val albumsAdapter by lazy { AlbumsAdapter(context!!) }
    private val similarArtistsAdapter by lazy { SimilarArtistsAdapter(context!!) }
    private var delegate: ArtistFragmentDelegate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerArtistComponent.builder()
            .artistModule(ArtistModule(this, context))
            .build()
            .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_artist, container, false)
    }

    override fun onStart() {
        super.onStart()
        artistPager.offscreenPageLimit = ARTIST_PAGER_ENTRIES_COUNT
        artistPager.adapter = ArtistPagerAdapter(context!!)
        artistPagerTabs.setupWithViewPager(artistPager)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        delegate = context as? MainActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.getString("artistId")?.let {
            with(presenter) {
                queryArtist(it)
                queryTopTracks(it, "ES")
                queryAlbums(it)
                querySimilarArtists(it)
            }
        }
    }

    override fun setArtistInfo(artist: Artist) {
        val monthlyListeners = String.format(getString(R.string.monthly_listeners, artist.followers.total))
        listeners.text = monthlyListeners
        collapsingLayout.title = artist.name
        Glide.with(context ?: return)
                .asBitmap()
                .load(artist.images.first().url)
                .into(object : SimpleTarget<Bitmap>(background.width, background.height) {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        background.setImageBitmap(resource)
                        resource.extractDominantSwatch()
                                .subscribeBy(
                                        onSuccess = { setArtistAccentColor(it) },
                                        onComplete = { artistPager.background = ColorDrawable(WHITE) }
                                )
                    }
                })
    }

    override fun setArtistTracks(tracks: List<Track>) {
        tracksAdapter.entries = tracks
    }

    override fun setArtistsAlbums(albums: List<Album>) {
        albumsAdapter.entries = albums
    }

    override fun setSimilarArtists(artists: List<Artist>) {
        similarArtistsAdapter.entries = artists
    }

    override fun showError(message: String) {
        //todo show lottie error
    }

    private fun setArtistAccentColor(swatch: Palette.Swatch) {
        val adjustedColor = adjustColorLightness(color = swatch.rgb, lightness = 0.3F)
        val transparentWhite = ContextCompat.getColor(context!!, R.color.transparentWhite)
        val blendedColor = ColorUtils.blendARGB(transparentWhite, swatch.rgb, 0.5F)
        createShader(artistPager, blendedColor)
        with(artistPagerTabs) {
            setSelectedTabIndicatorColor(adjustedColor);
            setTabTextColors(adjustedColor, adjustedColor)
        }
        with(collapsingLayout) {
            setContentScrimColor(adjustedColor)
            setStatusBarScrimColor(adjustedColor)
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
                        intArrayOf(color, WHITE, WHITE),
                        floatArrayOf(0F, 0.2f, 1f),
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

    //todo refactor this a bit maybe avoid using linear layout as a parent
    private inner class ArtistPagerAdapter(private val context: Context) : PagerAdapter() {

        private val inflater = LayoutInflater.from(context)

        override fun getCount() = ARTIST_PAGER_ENTRIES_COUNT

        override fun isViewFromObject(view: android.view.View, `object`: Any) = view == `object`

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layout = inflater.inflate(R.layout.artist_recycler, container, false) as LinearLayout
            when (position) {
                0 -> {
                    layout.artistRecycler.layoutManager = LinearLayoutManager(context)
                    layout.artistRecycler.adapter = tracksAdapter
                }
                1 -> {
                    val columnCount = getNumberOfColumns(context, columnWidth = 180)
                    val decoration = VerticalGridDecorator(context,16, columnCount)
                    layout.artistRecycler.addItemDecoration(decoration)
                    layout.artistRecycler.layoutManager = GridLayoutManager(context, columnCount, VERTICAL, false)
                    layout.artistRecycler.adapter = albumsAdapter
                }
                2 -> {
                    layout.artistRecycler.layoutManager = GridLayoutManager(context, 3, VERTICAL, false)
                    layout.artistRecycler.adapter = similarArtistsAdapter
                }
            }
            container.addView(layout)
            return layout
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
