package com.dandy.ugnius.dandy.model.clients

import com.dandy.ugnius.dandy.model.entities.Credentials
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.POST
import retrofit2.http.FormUrlEncoded



interface AuthClient {

    @FormUrlEncoded
    @POST("api/token")
    fun getCredentials(@Field("code") code: String): Single<Credentials>

    @FormUrlEncoded
    @POST("api/refresh_token")
    fun refreshToken(@Field("refresh_token") refreshToken: String): Single<Credentials>

}