package com.rx.ugnius.rx

import com.rx.ugnius.rx.artist.presenter.ArtistPresenter
import com.rx.ugnius.rx.artist.view.View
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
        val viewMock = mock(View::class.java)
        val artistPresenter = ArtistPresenter(viewMock)
        artistPresenter.queryArtistAlbums("6rYogEVj60BCIsLukpAnwr")
        artistPresenter.queryArtist("6rYogEVj60BCIsLukpAnwr")
        artistPresenter.queryArtistTopTracks("6rYogEVj60BCIsLukpAnwr", "ES")
        verify(viewMock, timeout(1000)).displayArtistAlbums(ArgumentMatchers.anyList())
        verify(viewMock, timeout(1000)).displayArtistInfo(any())
        verify(viewMock, timeout(1000)).displayArtistTracks(ArgumentMatchers.anyList())
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
