package com.dandy.ugnius.dandy

import android.os.Bundle
import com.dandy.ugnius.dandy.global.entities.Track
import com.dandy.ugnius.dandy.player.presenter.PlayerPresenter
import com.dandy.ugnius.dandy.player.view.PlayerView
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.Mockito.`when`
import org.junit.Before
import org.junit.rules.ExpectedException
import org.junit.Rule

class PlayerPresenterTest {

    private val tracks = getTracks()
    private lateinit var view: PlayerView
    private lateinit var presenter: PlayerPresenter
    private lateinit var bundle: Bundle

    @get:Rule
    var exceptions = ExpectedException.none()

    @Before
    fun init() {
        view = mock(PlayerView::class.java)
        bundle = mock(Bundle::class.java)
        presenter = PlayerPresenter(view)
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

    private fun getTracks(): ArrayList<Track> {
        val images = emptyList<String>()
        with(arrayListOf<Track>()) {
            add(Track(images, "ScHoolboy Q & 2 Chainz & Saudi", "04:27", true, "4LmAnpjlhWTahvRkYR8xJa", "X (with 2 Chainz & Saudi)", "spotify:track:4LmAnpjlhWTahvRkYR8xJa"))
            add(Track(images, "ScHoolboy Q & Kendrick Lamar", "04:59", true, "0zO8ctW0UiuOefR87OeJOZ", "Collard Greens", "spotify:track:0zO8ctW0UiuOefR87OeJOZ"))
            add(Track(images, "ScHoolboy Q", "03:36", true, "5SsR3wtCOafDmZgvIdRhSm", "Man Of The Year", "spotify:track:5SsR3wtCOafDmZgvIdRhSm"))
            add(Track(images, "ScHoolboy Q & Kanye West", "05:13", true, "2yJ9GVCLMmzBBfQAnfzlwr", "THat Part", "spotify:track:2yJ9GVCLMmzBBfQAnfzlwr"))
            add(Track(images, "ScHoolboy Q & BJ The Chicago Kid", "04:38", true, "29gsi1zZrZxdStACmTQB0Z", "Studio", "spotify:track:29gsi1zZrZxdStACmTQB0Z"))
            add(Track(images, "ScHoolboy Q & ASAP Rocky", "03:17", true, "78JKJfKsqgeBDBF58gv1SF", "Hands on the Wheel (feat. Asap Rocky)", "spotify:track:78JKJfKsqgeBDBF58gv1SF"))
            add(Track(images, "ScHoolboy Q & E-40", "03:42", true, "7pda6TLAbVGxUjIUtbiNt0", "Dope Dealer", "spotify:track:7pda6TLAbVGxUjIUtbiNt0"))
            add(Track(images, "ScHoolboy Q & Drake & Tinashe", "04:09", true, "267ZRvvVgsGPevJuvEGyug", "3 on (feat. Tinashe, Drake)", "spotify:track:267ZRvvVgsGPevJuvEGyug"))
            add(Track(images, "ScHoolboy Q", "04:32", true, "5DOR49R0fehoBmyNL8m4sN", "Hell Of A Night", "spotify:track:5DOR49R0fehoBmyNL8m4sN"))
            add(Track(images, "ScHoolboy Q & Kendrick Lamar", "04:20", true, "6TLSA1Tmw8RQxvAioq6je6", "Birds & The Beez", "spotify:track:6TLSA1Tmw8RQxvAioq6je6"))
            add(Track(images, "ScHoolboy Q", "04:48", true, "5sqHFfmw7MMc1L85BN8802", "THat Part - Black Hippy Remix", "spotify:track:5sqHFfmw7MMc1L85BN8802"))
            add(Track(images, "ScHoolboy Q", "05:34", true, "03S5vMBN07OOnPj1SCMsxV", "TorcH", "spotify:track:03S5vMBN07OOnPj1SCMsxV"))
            add(Track(images, "ScHoolboy Q", "01:44", true, "3uu93VCGbNfkXqUuDQNqaT", "Lord Have Mercy", "spotify:track:3uu93VCGbNfkXqUuDQNqaT"))
            add(Track(images, "ScHoolboy Q & Jadakiss", "06:19", true, "1MY4HVXfBkUg1mUjmCjznI", "Groovy Tony / Eddie Kane", "spotify:track:1MY4HVXfBkUg1mUjmCjznI"))
            add(Track(images, "ScHoolboy Q & Lance Skiiiwalker", "05:25", true, "3yg7fx2DVjbQRLoROq3wA8", "Kno Ya Wrong", "spotify:track:3yg7fx2DVjbQRLoROq3wA8"))
            add(Track(images, "ScHoolboy Q & Vince Staples", "04:47", true, "2glfyBSK3wqoumtEVN1F07", "Ride Out", "spotify:track:2glfyBSK3wqoumtEVN1F07"))
            add(Track(images, "ScHoolboy Q & Candice Pillay", "03:50", true, "44ZWfYPt3AQJAfg4Ned0ue", "WHateva U Want", "spotify:track:44ZWfYPt3AQJAfg4Ned0ue"))
            add(Track(images, "ScHoolboy Q", "03:34", true, "1iY2DB9Bpn1r8mBgoFLBOG", "By Any Means", "spotify:track:1iY2DB9Bpn1r8mBgoFLBOG"))
            add(Track(images, "ScHoolboy Q", "03:39", true, "7sr74Bz3GBTNMQe1m4F5Ut", "JoHn Muir", "spotify:track:7sr74Bz3GBTNMQe1m4F5Ut"))
            add(Track(images, "ScHoolboy Q & Tha Dogg Pound", "03:43", true, "7fQzRNL3k0YUkhTTSuZgcg", "Big Body", "spotify:track:7fQzRNL3k0YUkhTTSuZgcg"))
            return this
        }
    }

}