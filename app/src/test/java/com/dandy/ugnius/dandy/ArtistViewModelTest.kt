package com.dandy.ugnius.dandy

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.dandy.ugnius.dandy.TestData.getTestAlbums
import com.dandy.ugnius.dandy.artist.viewmodels.ArtistViewModel
import com.dandy.ugnius.dandy.global.clients.APIClient
import com.dandy.ugnius.dandy.global.entities.Album
import com.dandy.ugnius.dandy.global.entities.Artist
import com.dandy.ugnius.dandy.global.entities.Error
import com.dandy.ugnius.dandy.global.entities.Track
import io.reactivex.Observable
import com.dandy.ugnius.dandy.TestData.getTestArtist
import com.dandy.ugnius.dandy.TestData.getTestArtists
import com.dandy.ugnius.dandy.TestData.getTestTracks
import com.dandy.ugnius.dandy.utilities.second
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ArtistViewModelTest {

    companion object {

        private const val TEST_ARTIST_ID = "2iLpvtffIrQ4bMYrFPRN4x"
        private const val TEST_MARKET = "ES"
        private const val TEST_GROUPS = "albums, single"
        private const val TEST_ALBUM_ID = "0aSNqdAdFpfr1x0THc4zZL"

        @BeforeClass
        @JvmStatic
        fun overrideSchedulers() {
            println("flow: override schedulers ran")
            val immediate = object : Scheduler() {
                override fun createWorker() = ExecutorScheduler.ExecutorWorker(Runnable::run)
            }
            RxJavaPlugins.setInitIoSchedulerHandler { immediate }
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }
        }
    }

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var apiClient: APIClient
    @InjectMocks private lateinit var viewModel: ArtistViewModel

    private val testArtist = getTestArtist()
    private val testTracks = getTestTracks()
    private val testArtists = getTestArtists()
    private val testAlbums = getTestAlbums()

    @Before
    fun setup() {
        `when`(apiClient.getArtist(TEST_ARTIST_ID)).thenReturn(Single.just(testArtist))
        `when`(apiClient.getArtistTopTracks(TEST_ARTIST_ID, TEST_MARKET)).thenReturn(Observable.just(testTracks))
        `when`(apiClient.getSimilarArtists(TEST_ARTIST_ID)).thenReturn(Observable.just(testArtists))
        `when`(apiClient.getArtistAlbums(TEST_ARTIST_ID, TEST_GROUPS)).thenReturn(Observable.just(testAlbums))
        `when`(apiClient.getAlbumsTracks(TEST_ALBUM_ID)).thenReturn(Observable.just(testTracks))

    }


    @Test(expected = RuntimeException::class)
    fun testArtistViewModel() {
        val artistObserver = TestObserver<Artist>()
        val topTrackObserver = TestObserver<List<Track>>()
        val artistsObserver = TestObserver<List<Artist>>()
        val allTracksObserver = TestObserver<List<Track>>()
        val albumsObserver = TestObserver<List<Album>>()
        val errorObserver = TestObserver<Error>()

        viewModel.artist.observeForever(artistObserver)
        viewModel.topTracks.observeForever(topTrackObserver)
        viewModel.similarArtists.observeForever(artistsObserver)
        viewModel.tracks.observeForever(allTracksObserver)
        viewModel.albums.observeForever(albumsObserver)

        viewModel.query(TEST_ARTIST_ID, TEST_MARKET, TEST_GROUPS)

        //Artist assertion
        assertEquals(testArtist, artistObserver.observedValue)
        //TopTracks assertion
        assertEquals(testTracks, topTrackObserver.observedValue)
        //SimilarArtists assertion
        val firstArtist = artistsObserver.observedValue!!.first()
        val secondArtist = artistsObserver.observedValue!!.second()
        assertEquals(3, firstArtist.tracks!!.size)
        assertEquals(3, secondArtist.tracks!!.size)
        assertTrue(firstArtist.popularity <= secondArtist.popularity)
        //AllTracksAndAlbums assertion
        assertEquals(1, allTracksObserver.observedValue!!.size)
        assertEquals(2, albumsObserver.observedValue!!.size)
        val firstAlbum = albumsObserver.observedValue!!.first()
        val secondAlbum = albumsObserver.observedValue!!.second()
        assertEquals("2017-11-10", firstAlbum.releaseDate)
        assertEquals("2016-11-10", secondAlbum.releaseDate)

        `when`(apiClient.getArtist(TEST_ARTIST_ID)).thenThrow(RuntimeException("Exception"))
        viewModel.error.observeForever(errorObserver)
        viewModel.query(TEST_ARTIST_ID, TEST_MARKET, TEST_GROUPS)
        assertEquals("Exception", errorObserver.observedValue!!.message)

    }

}