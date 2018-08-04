package com.dandy.ugnius.dandy.login.views.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.di.components.DaggerLoginComponent
import com.dandy.ugnius.dandy.di.modules.LoginModule
import com.dandy.ugnius.dandy.di.modules.GeneralModule
import com.dandy.ugnius.dandy.login.events.UserCredentialsEnteredEvent
import com.dandy.ugnius.dandy.login.presenters.LoginPresenter
import com.dandy.ugnius.dandy.utilities.fade
import com.github.florent37.viewanimator.ViewAnimator
import kotlinx.android.synthetic.main.fragment_login.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe

interface LoginFragmentDelegate {
    fun onLoginButtonPressed()
    fun onAuthenticationSuccess()
}
interface LoginView {
    fun goToMainFragment()
    fun showError(message: String)
}
class LoginFragment : Fragment(), LoginView {

    @Inject lateinit var loginPresenter: LoginPresenter
    @Inject lateinit var eventBus: EventBus
    var delegate: LoginFragmentDelegate? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        DaggerLoginComponent.builder()
            .generalModule(GeneralModule(context!!))
            .loginModule(LoginModule(this))
            .build()
            .inject(this)

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewAnimator.animate(meetDandy)
            .fadeIn()
            .startDelay(1000)
            .duration(500)
            .andAnimate(description)
            .fadeIn()
            .thenAnimate(loginButton)
            .fadeIn()
            .translationY(-100F, 0F)
            .duration(500)
            .onStop { loginButton?.setOnClickListener { delegate?.onLoginButtonPressed() } }
            .start()
    }

    override fun onStart() {
        super.onStart()
        eventBus.register(this)
    }


    override fun onStop() {
        super.onStop()
        eventBus.unregister(this)
    }

    override fun showError(message: String) {
        loader?.playErrorAnimation(message) {
            fade(views = *arrayOf(meetDandy, description, loginButton), values = floatArrayOf(0F, 0.5F, 1F))
        }
    }

    override fun goToMainFragment() {
        delegate?.onAuthenticationSuccess()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserCredentialsEnteredEvent(event: UserCredentialsEnteredEvent) {
        fade(
            views = *arrayOf(meetDandy, description, loginButton),
            values = floatArrayOf(1F, 0.5F, 0F),
            onStart = { loginPresenter.login(event.code) },
            onStop = { loader?.playLoadingAnimation(context!!.getString(R.string.logging_in)) })
    }


}
