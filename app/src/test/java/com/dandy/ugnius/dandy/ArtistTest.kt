package com.dandy.ugnius.dandy

import com.dandy.ugnius.dandy.artist.presenter.ArtistPresenter
import com.dandy.ugnius.dandy.artist.view.ArtistView
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Test
import org.mockito.Mockito.*
import java.util.concurrent.TimeUnit
import io.reactivex.android.plugins.RxAndroidPlugins
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

class ArtistTest {

    @Test
    fun addition_isCorrect() {
        switchScheduler()
        val viewMock = mock(ArtistView::class.java)
        val artistPresenter = ArtistPresenter(viewMock)
        artistPresenter.queryArtistAlbums("6rYogEVj60BCIsLukpAnwr")
        artistPresenter.queryArtist("6rYogEVj60BCIsLukpAnwr")
        artistPresenter.queryArtistTopTracks("6rYogEVj60BCIsLukpAnwr", "ES")
        verify(viewMock, timeout(1000)).setArtistsAlbums(ArgumentMatchers.anyList())
        verify(viewMock, timeout(1000)).setArtistInfo(any())
        verify(viewMock, timeout(1000)).setArtistTracks(ArgumentMatchers.anyList())
    }

    private fun switchScheduler() {
        val immediate = object : Scheduler() {
            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Runnable::run)
            }

            override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
                return super.scheduleDirect(run, 0 , unit)
            }
        }
        RxJavaPlugins.setInitIoSchedulerHandler { immediate }
        RxJavaPlugins.setInitComputationSchedulerHandler { immediate }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { immediate }
        RxJavaPlugins.setInitSingleSchedulerHandler { immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }
    }

    private fun <T> any(): T {
        Mockito.any<T>()
        return null as T
    }
}