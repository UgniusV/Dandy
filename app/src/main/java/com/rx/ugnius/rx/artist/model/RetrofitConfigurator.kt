package com.rx.ugnius.rx.artist.model

import com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rx.ugnius.rx.artist.model.entities.Album
import com.rx.ugnius.rx.artist.model.entities.Artist
import com.rx.ugnius.rx.artist.model.entities.Track
import com.rx.ugnius.rx.artist.model.entities.deserializers.AlbumsDeserializer
import com.rx.ugnius.rx.artist.model.entities.deserializers.ArtistDeserializer
import com.rx.ugnius.rx.artist.model.entities.deserializers.TracksDeserializer
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConfigurator private constructor() {

    companion object {


        private const val ACCESS_TOKEN = " Bearer BQC_gtplWYt3oPgnLTCXO3ini1v9JDxt1p6zk8GhE-W02oN1QCAcxDQze-d0PLrLbgrxUKUpD1leue3Rw1ZwCP13bk378M387iQaugoDkbaDwWiOPrrCpJAJ82CAzwkLYq16dNWoa6U1ks2x8Rv6aLcBDmFNN25xWuBE6HhuW7sV1AWbqg"

        fun configure(): Retrofit = with(Retrofit.Builder()) {
            baseUrl("https://api.spotify.com")
            addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            addConverterFactory(createGsonConverterFactory())
            client(createOkHttpClient())
            build()
        }

        private fun createOkHttpClient() = with(OkHttpClient().newBuilder()) {
            addInterceptor { chain ->
                val originalRequest = chain.request()
                val builder = originalRequest.newBuilder().header("Authorization", ACCESS_TOKEN)
                val modifiedRequest = builder.build()
                chain.proceed(modifiedRequest)
            }
            build()
        }

        private fun createGsonConverterFactory(): GsonConverterFactory {
            val trackListType = TypeToken.getParameterized(List::class.java, Track::class.java).type
            val albumsListType = TypeToken.getParameterized(List::class.java, Album::class.java).type
            val artistsListType = TypeToken.getParameterized(List::class.java, Artist::class.java).type
            val builder = GsonBuilder()
            builder.setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
            builder.registerTypeAdapter(trackListType, TracksDeserializer())
            builder.registerTypeAdapter(albumsListType, AlbumsDeserializer())
            builder.registerTypeAdapter(artistsListType, ArtistDeserializer())
            return GsonConverterFactory.create(builder.create())
        }
    }

}