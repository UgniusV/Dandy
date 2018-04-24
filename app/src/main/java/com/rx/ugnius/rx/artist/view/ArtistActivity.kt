package com.rx.ugnius.rx.artist.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM
import android.graphics.drawable.GradientDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.rx.ugnius.rx.artist.view.ArtistPagerAdapter.Companion.ARTIST_PAGER_ENTRIES_COUNT
import android.graphics.Color.WHITE
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.rx.ugnius.rx.R
import com.rx.ugnius.rx.artist.ArtistPresenter
import com.rx.ugnius.rx.artist.model.entities.Album
import com.rx.ugnius.rx.artist.model.entities.Artist
import com.rx.ugnius.rx.artist.model.entities.Track
import com.rx.ugnius.rx.extractDominantSwatch
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.view_artist.*
import kotlinx.android.synthetic.main.artist_header.*

class ArtistActivity : AppCompatActivity(), View {

    private val presenter = ArtistPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_artist)
        artistPager.offscreenPageLimit = ARTIST_PAGER_ENTRIES_COUNT
        artistPager.adapter = ArtistPagerAdapter(this)
        presenter.queryArtist("6rYogEVj60BCIsLukpAnwr")
        presenter.queryArtistAlbums("6rYogEVj60BCIsLukpAnwr")
        presenter.queryArtistTopTracks("6rYogEVj60BCIsLukpAnwr")

    }

    override fun displayArtistInfo(artist: Artist) {
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
                                        onSuccess = { artistPager.background = createWhiteBlendedGradient(it.rgb) },
                                        onComplete = { artistPager.background = createWhiteBlendedGradient(WHITE) }
                                )

                    }
                })
    }

    private fun createWhiteBlendedGradient(blendColor: Int): GradientDrawable {
        val transparentWhite = ContextCompat.getColor(applicationContext, R.color.transparentWhite)
        val blendedColor = ColorUtils.blendARGB(transparentWhite, blendColor, 0.4F)
        return GradientDrawable(TOP_BOTTOM, intArrayOf(blendedColor, transparentWhite))
    }

    override fun displayArtistTopTracks(tracks: List<Track>) {
//        tracksRecycler.layoutManager = LinearLayoutManager(this)
//        tracksRecycler.adapter = TracksAdapter(tracks, this)
    }

    override fun displayArtistAlbums(albums: List<Album>) {
        //todo show artist albums
    }

    override fun showError(message: String) {
        //todo show lottie error
    }

    class ArtistPagerAdapter(private val context: Context) : PagerAdapter() {

        private val inflater = LayoutInflater.from(context)

        companion object {
            const val ARTIST_PAGER_ENTRIES_COUNT = 1
        }

        override fun getCount() = ARTIST_PAGER_ENTRIES_COUNT

        override fun isViewFromObject(view: android.view.View, `object`: Any) = view == `object`


        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = inflater.inflate(R.layout.artist_recycler, container, false)
            (view as? RecyclerView)?.let {
                it.layoutManager = LinearLayoutManager(context)
                it.adapter
            }
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as android.view.View)
        }

        override fun getPageTitle(position: Int) = when (position) {
            0 -> "first page"
            1 -> "second page"
            2 -> "third page"
            else -> throw IllegalArgumentException("Invalid item count was specified")
        }
    }
}
