package com.rx.ugnius.rx.artist.di

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rx.ugnius.rx.artist.model.entities.Album
import com.rx.ugnius.rx.artist.model.entities.Artist
import com.rx.ugnius.rx.artist.model.entities.Track
import com.rx.ugnius.rx.artist.model.entities.deserializers.AlbumsDeserializer
import com.rx.ugnius.rx.artist.model.entities.deserializers.ArtistDeserializer
import com.rx.ugnius.rx.artist.model.entities.deserializers.TracksDeserializer
import dagger.Module
import dagger.Provides
import com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
import com.rx.ugnius.rx.artist.model.ArtistClient
import com.rx.ugnius.rx.artist.presenter.ArtistPresenter
import com.rx.ugnius.rx.artist.view.ArtistView
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ArtistModule(private val artistView: ArtistView) {

    //todo look into scopes
    @Provides
    fun provideArtistPresenter(artistClient: ArtistClient) = ArtistPresenter(artistClient, artistView)

    @Provides
    fun provideOkHttpClient() = with(OkHttpClient().newBuilder()) {
        val accessToken = " Bearer BQAfm-Wtm3enyiK3qgbKxuY9b-DvOwf0pbgF3e_kMX2xvI3awqZ9eidAOFI7KbzhE1yByxVzCFNF00JYjeT4QJHO03BHc5EZBVh305EsfkYkmlBI-5IbiSdmVScjTusLFcRwW-L6hmei_rwuHRT4hgwV2Q3JtcCCu1w1vMCIG72XbGt40g"
        addInterceptor { chain ->
            val originalRequest = chain.request()
            val builder = originalRequest.newBuilder().header("Authorization", accessToken)
            val modifiedRequest = builder.build()
            chain.proceed(modifiedRequest)
        }
        build()
    }

    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory {
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

    @Provides
    fun provideRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ) = with(Retrofit.Builder()) {
        baseUrl("https://api.spotify.com")
        addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        addConverterFactory(gsonConverterFactory)
        client(okHttpClient)
        build()
    }

    @Provides
    fun provideArtistClient(retrofit: Retrofit) = retrofit.create(ArtistClient::class.java)

}