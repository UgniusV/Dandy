package com.dandy.ugnius.dandy.login.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.App
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.login.interfaces.LoginView
import com.dandy.ugnius.dandy.login.model.Event
import com.dandy.ugnius.dandy.login.viewmodel.LoginViewModel
import com.dandy.ugnius.dandy.login.viewmodel.ViewModelFactory
import com.dandy.ugnius.dandy.main.MainActivity
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

interface LoginFragmentDelegate {
    fun onLoginClicked()
}

class LoginFragment : Fragment(), LoginView, LoginFragmentDelegate {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    private var viewModel: LoginViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.applicationContext as App).mainComponent?.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)
        viewModel?.event?.observe(this, Observer<Event> {
            if (it!!.isSuccessful) {
                (activity as MainActivity).openLibraryFragment()
            } else {
                println("event is unseefcul")
            }
        })
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //todo apply databinding
        discoChicken.setOnClickListener { onLoginClicked() }
    }

    fun login(code: String) {
        viewModel?.login(code)
    }

    override fun onLoginClicked() {
        val builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.CODE, REDIRECT_URI)
        builder.setScopes(Utilities.getScopes())
        val request = builder.build()
        AuthenticationClient.openLoginActivity(activity, LOGIN_REQUEST_CODE, request)
    }


    override fun goToMainFragment() {
        //go to main fragment
    }

    override fun showError() {
        //show error
    }
}
