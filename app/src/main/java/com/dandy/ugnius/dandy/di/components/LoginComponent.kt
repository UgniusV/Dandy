package com.dandy.ugnius.dandy.di.components

import com.dandy.ugnius.dandy.di.modules.GeneralModule
import com.dandy.ugnius.dandy.di.modules.LoginModule
import com.dandy.ugnius.dandy.login.views.fragments.LoginFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [LoginModule::class, GeneralModule::class])
interface LoginComponent {
    fun inject(loginFragment: LoginFragment)
}