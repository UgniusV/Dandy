package com.dandy.ugnius.dandy

import android.os.Bundle
import com.dandy.ugnius.dandy.global.entities.Track
import com.dandy.ugnius.dandy.player.presenters.PlayerPresenter
import org.junit.Test
import com.dandy.ugnius.dandy.TestData.getPlaybackTestTrackQueue
import com.dandy.ugnius.dandy.player.view.PlayerView
import com.dandy.ugnius.dandy.utilities.fourth
import com.dandy.ugnius.dandy.utilities.second
import com.dandy.ugnius.dandy.utilities.third
import org.mockito.Mockito.*
import org.mockito.Mockito.`when`
import org.junit.Before
import org.junit.rules.ExpectedException
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PlayerPresenterTest {

    @get:Rule
    var exceptions: ExpectedException = ExpectedException.none()

    @Mock private lateinit var view: PlayerView
    @Mock private lateinit var bundle: Bundle
    @InjectMocks private lateinit var presenter: PlayerPresenter

    private val tracks = getPlaybackTestTrackQueue()

    @Before
    fun init() {
        `when`(bundle.getParcelableArrayList<Track>("tracks")).thenReturn(tracks)
        `when`(bundle.getParcelable<Track>("currentTrack")).thenReturn(tracks.first())
        presenter.setState(bundle)
        presenter.playTrack()
    }

    @Test
    fun testRegularSkipping() {
        with(presenter) {
            skipToNext()
            skipToNext()
            skipToNext()
            skipToPrevious()
            skipToPrevious()
            skipToPrevious()
            skipToPrevious()
        }
        with(view) {
            verify(this, times(3)).update(tracks.first())
            verify(this, times(2)).update(tracks.second())
            verify(this, times(2)).update(tracks.third())
            verify(this).update(tracks.fourth())
        }
    }

    @Test
    fun testShuffleSkipping() {
        var firstShuffleTrack: Track? = null
        var secondShuffleTrack: Track? = null
        var thirdShuffleTrack: Track? = null
        with(presenter) {
            skipToNext()
            skipToNext()
            toggleShuffle()
            firstShuffleTrack = skipToNext()
            secondShuffleTrack = skipToNext()
            skipToPrevious()
            skipToPrevious()
            skipToPrevious()
            skipToPrevious()
            skipToPrevious()
            skipToNext()
            skipToNext()
            skipToNext()
            skipToNext()
            thirdShuffleTrack = skipToNext()
            toggleShuffle()
            skipToPrevious()
            skipToNext()
            skipToNext()
        }

        with(view) {
            verify(this).toggleShuffle(true)
            verify(this).toggleShuffle(false)
            verify(this, atLeast(3)).update(tracks.first())
            verify(this, atLeast(3)).update(tracks.second())
            verify(this, atLeast(3)).update(tracks.third())
            verify(this, atLeast(3)).update(firstShuffleTrack!!)
            verify(this, atLeast(2)).update(secondShuffleTrack!!)
            verify(this, atLeast(2)).update(thirdShuffleTrack!!)
            if (tracks.indexOf(thirdShuffleTrack!!) !in 1..18) {
                exceptions.expect(IndexOutOfBoundsException::class.java)
            }
            verify(this, atLeast(1)).update(tracks[tracks.indexOf(thirdShuffleTrack!!) - 1])
            verify(this, atLeast(1)).update(tracks[tracks.indexOf(thirdShuffleTrack!!) + 1])
        }
    }

    @Test
    fun testPlaybackToggling() {
        presenter.skipToNext()
        presenter.togglePlayback()
        presenter.skipToNext()
        presenter.togglePlayback()
        presenter.skipToPrevious()
        verify(view, times(2)).togglePlayButton(true)
        verify(view, times(4)).togglePlayButton(false)
    }

    @Test
    fun testReplaying() {
        presenter.toggleReplay()
        `when`(view.hasTrackEnded()).thenReturn(true)
        presenter.skipToNext()
        `when`(view.hasTrackEnded()).thenReturn(false)
        presenter.skipToNext()
        presenter.skipToPrevious()
        verify(view, times(3)).update(tracks.first())
        verify(view).update(tracks.second())
        verify(view).toggleReplay(true)
    }

    @Test
    fun testPlaybackQueueCompletion() {
        for (i in 0 until 21) {
            presenter.skipToNext()
        }
        verify(view, times(2)).update(tracks.first())
    }


}