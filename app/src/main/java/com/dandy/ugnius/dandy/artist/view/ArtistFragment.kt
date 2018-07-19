package com.dandy.ugnius.dandy.artist.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color.WHITE
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v4.view.PagerAdapter
import android.support.v7.graphics.Palette
import android.support.v7.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dandy.ugnius.dandy.model.entities.Album
import com.dandy.ugnius.dandy.model.entities.Artist
import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.artist.presenter.ArtistPresenter
import android.view.*
import com.App
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.model.clients.APIClient
import com.dandy.ugnius.dandy.artist.view.adapters.AlbumsAdapter
import com.dandy.ugnius.dandy.artist.view.adapters.SimilarArtistsAdapter
import com.dandy.ugnius.dandy.artist.view.adapters.TracksAdapter
import com.dandy.ugnius.dandy.artist.view.interfaces.ArtistFragmentDelegate
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.view_artist.*
import com.dandy.ugnius.dandy.main.MainActivity
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject
import com.dandy.ugnius.dandy.artist.view.decorations.ItemOffsetDecoration
import android.support.v7.widget.RecyclerView


class ArtistFragment : Fragment(), ArtistView {

    private companion object {
        const val ARTIST_PAGER_ENTRIES_COUNT = 3
    }

    @Inject
    lateinit var apiClient: APIClient
    private val formatter = NumberFormat.getNumberInstance(Locale.US)
    private val presenter by lazy { ArtistPresenter(apiClient, this) }
    private val tracksAdapter by lazy { TracksAdapter(context!!, { currentTrack -> (this::onArtistTrackClicked)(currentTrack) }) }
    private val albumsAdapter by lazy {
        AlbumsAdapter(context!!)
    }


    private val similarArtistsAdapter by lazy { SimilarArtistsAdapter(context!!) }
    private var delegate: ArtistFragmentDelegate? = null
    private var allTracks: List<Track>? = null
    private var topTracks: List<Track>? = null

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        (activity as MainActivity).menuInflater.inflate(R.menu.artist_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun onFavoriteClicked() {

    }

    private fun onAlbumClicked() {

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.actionFavorite) {
            println("favorite was clicked")
            true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.applicationContext as App).mainComponent?.inject(this)
        return inflater.inflate(R.layout.view_artist, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        delegate = context as? MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString("artistId")?.let { presenter.query(it, "ES", "album,single") }
        artistPager.offscreenPageLimit = ARTIST_PAGER_ENTRIES_COUNT
        artistPager.adapter = ArtistPagerAdapter(context!!)
        artistPagerTabs.setupWithViewPager(artistPager)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        with(activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun setAllTracks(allTracks: List<Track>) {
        this.allTracks = allTracks
    }

    private fun onArtistTrackClicked(currentTrack: Track) {
        allTracks?.let { delegate?.onArtistTrackClicked(currentTrack, it) }
    }

    override fun setArtistInfo(artist: Artist) {
        val followers = formatter.format(artist.followers)
        listeners.text = String.format(getString(R.string.followers, followers))
        collapsingLayout.title = artist.name
        loadImage(artist.images.first())
    }

    private fun loadImage(url: String) {
        background.post {
            Glide.with(context ?: return@post)
                .asBitmap()
                .load(url)
                .into(object : SimpleTarget<Bitmap>(background.width, background.height) {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        background.setImageBitmap(resource)
                        resource.extractSwatch().subscribeBy(
                            onSuccess = { setAccentColor(it) },
                            onComplete = { artistPager.background = ColorDrawable(WHITE) }
                        )
                    }
                })
        }
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

    override fun showLoader() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideLoader() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun setAccentColor(swatch: Palette.Swatch) {
        val adjustedColor = adjustColorLuminance(color = swatch.rgb, luminance = 1F)
        val transparentWhite = ContextCompat.getColor(context!!, R.color.transparentWhite)
        val blendedColor = ColorUtils.blendARGB(transparentWhite, swatch.rgb, 0.2F)
        artistPager.shade(color = blendedColor, ratio = 0.2F)

        artistPagerTabs?.let {
            it.setSelectedTabIndicatorColor(adjustedColor);
            it.setTabTextColors(adjustedColor, adjustedColor)
        }
        collapsingLayout?.let {
            it.setContentScrimColor(swatch.rgb)
            it.setStatusBarScrimColor(swatch.rgb)
        }
    }

    private inner class ArtistPagerAdapter(private val context: Context) : PagerAdapter() {

        private val inflater = LayoutInflater.from(context)

        override fun getCount() = ARTIST_PAGER_ENTRIES_COUNT

        override fun isViewFromObject(view: android.view.View, `object`: Any) = view == `object`

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val artistRecycler = inflater.inflate(R.layout.artist_recycler, container, false) as RecyclerView
            (artistRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            when (position) {
                0 -> {
                    artistRecycler.layoutManager = LinearLayoutManager(context)
                    artistRecycler.adapter = tracksAdapter
                }
                1 -> {
                    artistRecycler.layoutManager = GridLayoutManager(context, 2)
                    artistRecycler.adapter = albumsAdapter
                    artistRecycler.addItemDecoration(ItemOffsetDecoration(context, 4))
                }
                2 -> {
                    artistRecycler.adapter = similarArtistsAdapter
                    artistRecycler.layoutManager = LinearLayoutManager(context)
                }
            }
            container.addView(artistRecycler)
            return artistRecycler
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
