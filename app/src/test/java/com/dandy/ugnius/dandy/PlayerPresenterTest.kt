package com.dandy.ugnius.dandy

import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.player.presenter.PlayerPresenter
import com.dandy.ugnius.dandy.player.view.PlayerView
import com.spotify.sdk.android.player.SpotifyPlayer
import org.junit.Test
import org.mockito.Mockito.*
import java.util.concurrent.TimeUnit
import io.reactivex.android.plugins.RxAndroidPlugins
import junit.framework.Assert
import org.mockito.ArgumentMatchers
import org.mockito.Mockito


class PlayerPresenterTest {

    private val tracks = getTracks()

    @Test
    fun testSkippingWithoutShuffling() {

        //todo situos iskelti i virsu
        val viewMock = mock(PlayerView::class.java)
        val presenter = PlayerPresenter(viewMock)

        //next skipping
        presenter.skipToNext(currentTrack = tracks[0], tracks = tracks, shuffle = false, queue = emptySet(), randomNumber = 0)
        verify(viewMock).update(tracks[1])
        presenter.skipToNext(currentTrack = tracks[1], tracks = tracks, shuffle = false, queue = emptySet(), randomNumber = 0)
        verify(viewMock).update(tracks[2])
        presenter.skipToNext(currentTrack = tracks[2], tracks = tracks, shuffle = false, queue = emptySet(), randomNumber = 0)
        verify(viewMock).update(tracks[3])
        presenter.skipToNext(currentTrack = tracks[3], tracks = tracks, shuffle = false, queue = emptySet(), randomNumber = 0)
        verify(viewMock).update(tracks[4])
        presenter.skipToNext(currentTrack = tracks[4], tracks = tracks, shuffle = false, queue = emptySet(), randomNumber = 0)
        verify(viewMock).update(tracks[5])
        presenter.skipToNext(currentTrack = tracks[5], tracks = tracks, shuffle = false, queue = emptySet(), randomNumber = 0)
        verify(viewMock).update(tracks[6])

        //previous skipping
        presenter.skipToPrevious(currentTrack = tracks[6], tracks = tracks, shuffle = false, queue = emptySet())
        verify(viewMock, times(2)).update(tracks[5])
        presenter.skipToPrevious(currentTrack = tracks[5], tracks = tracks, shuffle = false, queue = emptySet())
        verify(viewMock, times(2)).update(tracks[4])
        presenter.skipToPrevious(currentTrack = tracks[4], tracks = tracks, shuffle = false, queue = emptySet())
        verify(viewMock, times(2)).update(tracks[3])
        presenter.skipToPrevious(currentTrack = tracks[3], tracks = tracks, shuffle = false, queue = emptySet())
        verify(viewMock, times(2)).update(tracks[2])
        presenter.skipToPrevious(currentTrack = tracks[2], tracks = tracks, shuffle = false, queue = emptySet())
        verify(viewMock, times(2)).update(tracks[1])
        presenter.skipToPrevious(currentTrack = tracks[1], tracks = tracks, shuffle = false, queue = emptySet())
        verify(viewMock).update(tracks[0])
        presenter.skipToPrevious(currentTrack = tracks[0], tracks = tracks, shuffle = false, queue = emptySet())
        verify(viewMock, times(2)).update(tracks[0])
    }


    //todo galbut pasidaryti koki play metoda

    @Test
    fun testSkippingWithShuffling() {
        val viewMock = mock(PlayerView::class.java)
        val presenter = PlayerPresenter(viewMock)
        val queue = LinkedHashSet<Track>()
        queue.add(tracks[0])
        presenter.skipToNext(currentTrack = tracks[0], tracks = tracks, shuffle = true, queue = queue, randomNumber = 3)
        queue.add(tracks[3])
        verify(viewMock).update(tracks[3])
        presenter.skipToNext(currentTrack = tracks[3], tracks = tracks, shuffle = true, queue = queue, randomNumber = 7)
        queue.add(tracks[7])
        verify(viewMock).update(tracks[7])
        presenter.skipToNext(currentTrack = tracks[7], tracks = tracks, shuffle = true, queue = queue, randomNumber = 14)
        queue.add(tracks[14])
        verify(viewMock).update(tracks[14])
        presenter.skipToNext(currentTrack = tracks[14], tracks = tracks, shuffle = true, queue = queue, randomNumber = 12)
        queue.add(tracks[12])
        verify(viewMock).update(tracks[12])

        presenter.skipToPrevious(currentTrack = tracks[12], tracks = tracks, shuffle = true, queue = queue)
        verify(viewMock, times(2)).update(tracks[14])
        presenter.skipToPrevious(currentTrack = tracks[14], tracks = tracks, shuffle = true, queue = queue)
        verify(viewMock, times(2)).update(tracks[7])
        presenter.skipToPrevious(currentTrack = tracks[7], tracks = tracks, shuffle = true, queue = queue)
        verify(viewMock, times(2)).update(tracks[3])
        presenter.skipToPrevious(currentTrack = tracks[3], tracks = tracks, shuffle = true, queue = queue)
        verify(viewMock).update(tracks[0])
        presenter.skipToPrevious(currentTrack = tracks[0], tracks = tracks, shuffle = true, queue = queue)
        verify(viewMock, times(2)).update(tracks[0])
    }

    private fun getTracks(): List<Track> {
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