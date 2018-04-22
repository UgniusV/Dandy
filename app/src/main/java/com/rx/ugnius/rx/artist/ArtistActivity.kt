package com.rx.ugnius.rx.artist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.rx.ugnius.rx.R
import com.rx.ugnius.rx.api.APIClient
import com.rx.ugnius.rx.api.RetrofitConfigurator
import com.rx.ugnius.rx.api.entities.Album
import com.rx.ugnius.rx.api.entities.Artist
import com.rx.ugnius.rx.api.entities.Track
import kotlinx.android.synthetic.main.view_artist.*
import kotlinx.android.synthetic.main.artist_header.*

//"6rYogEVj60BCIsLukpAnwr"

class ArtistActivity : AppCompatActivity(), ArtistViewAware {

    private val presenter = ArtistPresenter(this, ArtistInteractor())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_artist)

    }


    override fun displayArtistInfo(artist: Artist) {
        val artistImage = artist.images.firstOrNull()?.url
        val monthlyListeners = String.format(getString(R.string.monthly_listeners, artist.followers.total))
        artistImage?.let { Glide.with(this).load(it).into(background) }
        name.text = artist.name
        listeners.text = monthlyListeners
    }

    override fun displayArtistTopTracks(tracks: List<Track>) {
        tracksRecycler.layoutManager = LinearLayoutManager(this)
        tracksRecycler.adapter = TracksAdapter(tracks, this)
    }

    override fun displayArtistAlbums(albums: Album) {
        //todo show artist albums
    }

    override fun showError(message: String) {
        //todo show lottie error
    }
}
