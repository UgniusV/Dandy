package com.dandy.ugnius.dandy.di.modules

import com.dandy.ugnius.dandy.global.clients.APIClient
import com.dandy.ugnius.dandy.global.repositories.Repository
import com.dandy.ugnius.dandy.login.interfaces.LoginView
import com.dandy.ugnius.dandy.login.model.clients.AuthenticationClient
import com.dandy.ugnius.dandy.login.presenters.LoginPresenter
import dagger.Module
import dagger.Provides

@Module(includes = [NetworkModule::class])
class LoginModule(private val loginView: LoginView) {

    @Provides
    fun provideLoginPresenter(
        authenticationClient:AuthenticationClient,
        apiClient: APIClient,
        repository: Repository
    ) = LoginPresenter(loginView, authenticationClient, apiClient, repository)
}