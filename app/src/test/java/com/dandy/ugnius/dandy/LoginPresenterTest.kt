package com.dandy.ugnius.dandy

import android.content.SharedPreferences
import com.dandy.ugnius.dandy.login.interfaces.LoginView
import com.dandy.ugnius.dandy.login.presenter.LoginPresenter
import com.dandy.ugnius.dandy.model.clients.AuthClient
import com.dandy.ugnius.dandy.model.entities.Credentials
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Before
import org.mockito.ArgumentMatchers.anyList
import org.junit.Test
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

class LoginPresenterTest {

    companion object {

        @BeforeClass
        @JvmStatic
        fun overrideSchedulers() {
            val immediate = object : Scheduler() {
                override fun createWorker() = ExecutorScheduler.ExecutorWorker(Runnable::run)
            }
            RxJavaPlugins.setInitIoSchedulerHandler { immediate }
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }
        }
    }


    @Test
    fun testSuccessfulLogin() {
        val authClient = mock(AuthClient::class.java)
        val view = mock(LoginView::class.java)
        val preferences = mock(SharedPreferences::class.java)
        val presenter = LoginPresenter(view, authClient, preferences)
        `when`(authClient.getCredentials("test")).thenReturn(Single.just(Credentials("test", "test")))
        presenter.login("test")
        verify(view).goToMainFragment()
    }

}
