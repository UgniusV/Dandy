package com.dandy.ugnius.dandy.likes.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dandy.ugnius.dandy.LIKES_PAGER_ENTRIES_COUNT
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.artist.view.adapters.AlbumsAdapterDelegate
import com.dandy.ugnius.dandy.artist.view.adapters.TracksAdapterDelegate
import com.dandy.ugnius.dandy.di.components.DaggerViewModelComponent
import com.dandy.ugnius.dandy.di.modules.GeneralModule
import com.dandy.ugnius.dandy.global.entities.Album
import com.dandy.ugnius.dandy.global.entities.Track
import com.dandy.ugnius.dandy.global.factories.ViewModelFactory
import com.dandy.ugnius.dandy.likes.view.adapters.LikesPagerAdapter
import com.dandy.ugnius.dandy.likes.viewmodels.LikesViewModel
import com.dandy.ugnius.dandy.observe
import com.dandy.ugnius.dandy.withViewModel
import kotlinx.android.synthetic.main.fragment_likes.*
import javax.inject.Inject

interface LikesFragmentDelegate {
    fun onTrackClicked(currentTrack: Track, allTracks: List<Track>)
}
class LikesFragment : Fragment(), TracksAdapterDelegate, AlbumsAdapterDelegate {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    private var viewModel: LikesViewModel? = null
    private var delegate: LikesFragmentDelegate? = null
    private val likesAdapter by lazy { LikesPagerAdapter(context!!, this, this) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        DaggerViewModelComponent.builder()
            .generalModule(GeneralModule(context!!))
            .build()
            .inject(this)
        viewModel = withViewModel(viewModelFactory) {
            observe(getTracks()) { likesAdapter.tracksAdapter.setTracks(it!!) }
            observe(getAlbums()) { likesAdapter.albumsAdapter.setAlbums(it!!) }
        }
        return inflater.inflate(R.layout.fragment_likes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.requestApplyInsets(root)
        likesPager.offscreenPageLimit = LIKES_PAGER_ENTRIES_COUNT
        likesPager.adapter = likesAdapter
        likesPagerTabs.setupWithViewPager(likesPager)
    }

    override fun onAlbumClicked(album: Album) {
        //todo open albums window
    }

    override fun onTrackClicked(currentTrack: Track, allTracks: List<Track>) {
        delegate?.onTrackClicked(currentTrack, allTracks)
    }

}