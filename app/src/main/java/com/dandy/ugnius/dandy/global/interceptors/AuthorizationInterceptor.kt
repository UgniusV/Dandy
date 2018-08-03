package com.dandy.ugnius.dandy.global.interceptors

import com.dandy.ugnius.dandy.global.repositories.Repository
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(private val repository: Repository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = repository.getCredentials()?.accessToken
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder().header("Authorization", " Bearer $accessToken")
        val modifiedRequest = builder.build()
        return chain.proceed(modifiedRequest)
    }

}