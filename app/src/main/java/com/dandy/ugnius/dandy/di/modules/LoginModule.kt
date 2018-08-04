package com.dandy.ugnius.dandy.di.modules

import com.dandy.ugnius.dandy.login.views.fragments.LoginView
import dagger.Module
import dagger.Provides

@Module(includes = [NetworkModule::class])
class LoginModule(private val loginView: LoginView) {

    @Provides
    fun provideLoginView() = loginView

}