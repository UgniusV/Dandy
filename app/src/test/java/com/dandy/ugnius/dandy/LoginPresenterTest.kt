package com.dandy.ugnius.dandy

import com.dandy.ugnius.dandy.global.clients.APIClient
import com.dandy.ugnius.dandy.global.entities.Credentials
import com.dandy.ugnius.dandy.global.repositories.Repository
import com.dandy.ugnius.dandy.login.model.clients.AuthenticationClient
import com.dandy.ugnius.dandy.login.presenters.LoginPresenter
import io.reactivex.Observable
import io.reactivex.Scheduler
import com.dandy.ugnius.dandy.TestData.getTestAlbums
import com.dandy.ugnius.dandy.TestData.getTestArtists
import com.dandy.ugnius.dandy.TestData.getTestTracks
import com.dandy.ugnius.dandy.login.views.fragments.LoginView
import io.reactivex.Single
import org.mockito.ArgumentMatchers.anyString
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Test
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginPresenterTest {

    companion object {

        private const val TEST_VALID_CODE = "AQB3ebJ_KGaqzH6XZ5TWMOVsdnDIgdlSwp7wY0xYPmjLvaO51WPt-BltIGXSoSJUNpYVldc9r3DccFuuNzvop1a2RR3iMKHNCcehnzdhrDjxO_nn4PgeeUqIroe6t4-3Q65CzLW0lNPZbQ6pbvRVfjTN_rboCj16jn7ovIzOllAiN6LXd_QyDG3gTVnqC20qLNwXcYH8QJsSMmG2VydgKxCnwlJwxOuDXKG3f100i4Fi4EnY8iffarY1gFp908qmqsuSQU9yMAxU9vin4_QRu1wFxx0D79z2YeBBebtLuZkLEn1ul9J8SGeMj5Svaz-7JIC1-ToW2SHYEmbfwkh0UE0fj86ZlcOpSOWlkyJhcY8D1_-YNLPor__OPEAeHItTMrSQb8onTiy-7Z4dClkCxt3lrq1u9Lo_fbqA_y63wfX-ubRV1pl0zmK7TNjNoCGlDbTz0Q650QqDDwz2sW8m4fLyo6k7E8FtyViTH25_pSPofjIFmDofhNcg6vp4mjsvE6ZsR7iNt5Im6jsGuXcyHZ_gJPu-rYyI9RstgyYZnLJl0gWebmO3sbkKmbl5wLxk9KCiy_N5Xte6CRb0L_y_BtIBaSLKxEfjtIPVziKvELt_vfhroZWnHUAJq06_VknSRlFIcyQ0O0m18Oy_t_XU6xDBnjmG32ijMeCvCMr7OW87aIk9DWHIGA"
        private const val TEST_INVALID_CODE = "ERROR"
        private const val TEST_ACCESS_TOKEN = "BQCDg30IVcf7iqhS_4JmqjLMBwWj505Xs_kf3mPDyJ6Dw8tkOXnxkQ9PP4OeDBpWy82H9blOxpfbfRatNZj49P8EdQC6GK7tn5poEBlyCRRqNHi65euGMIBleg9KjSBU4XPYTs73UqqJf8xzo4s82pTpIGHQvulXwXKTHY7GMVghqgfaGt55fFW20vjbrVBNpp7dKJ5ZMvzmJKLEV6-zv7HAKFTjbkg5e6-Hz8eEWlX83KJm1LGQyK2MGfQCY51_HeIuFAtZ7JfXqIRMbkdEXbGt"
        private const val TEST_REFRESH_TOKEN = "4j/JYXIzN24mDV3s0T0xP54IFlrj4llM9WC2Ptme0Lyrxaduq3u8wHtMdwanSfSLIHRd8mKs3ZY0yYrB66nsT4zCo0+n0rPp2eyIxRhsDOV4W9dMtLMu/wVImXDglSLnr+zKO5QvV8Blyw+0wD+qxFCWXf5Vz/n7ZS5gkHYQM725wvubyFivRw=="
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

    @Mock private lateinit var loginView: LoginView
    @Mock private lateinit var authenticationClient: AuthenticationClient
    @Mock private lateinit var apiClient: APIClient
    @Mock private lateinit var repository: Repository
    @InjectMocks private lateinit var presenter: LoginPresenter

    private val testAlbums = getTestAlbums()
    private val testTracks = getTestTracks()
    private val testArtists = getTestArtists()

    @Test(expected = RuntimeException::class)
    fun testUnsuccessfulLogin() {
        `when`(authenticationClient.getCredentials(TEST_INVALID_CODE)).thenThrow(RuntimeException())
        presenter.login(TEST_INVALID_CODE)
        verify(loginView).showError(anyString())

    }

    @Test
    fun testSuccessfulLogin() {
        val credentials = Credentials(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN)
        `when`(authenticationClient.getCredentials(TEST_VALID_CODE)).thenReturn(Observable.just(credentials))
        `when`(apiClient.getSavedTracks(0, 50)).thenReturn(Single.just(testTracks))
        `when`(apiClient.getSavedAlbums()).thenReturn(Observable.just(testAlbums))
        `when`(apiClient.getAlbumsTracks(TEST_ALBUM_ID)).thenReturn(Observable.just(testTracks))
        `when`(apiClient.getFollowingArtists("artist")).thenReturn(Single.just(testArtists))
        presenter.login(TEST_VALID_CODE)
        verify(repository).insertCredentials(credentials)
        verify(repository).insertTracks(testTracks)
        verify(repository).insertAlbums(testAlbums)
        verify(repository).insertArtists(testArtists)
        verify(loginView).goToMainFragment()
    }

}
