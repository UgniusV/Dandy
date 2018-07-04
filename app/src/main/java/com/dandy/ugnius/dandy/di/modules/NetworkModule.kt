package com.dandy.ugnius.dandy.di.modules

import android.content.SharedPreferences
import com.dandy.ugnius.dandy.artist.model.APIClient
import com.dandy.ugnius.dandy.artist.model.entities.Album
import com.dandy.ugnius.dandy.artist.model.entities.Artist
import com.dandy.ugnius.dandy.artist.model.entities.Track
import com.dandy.ugnius.dandy.artist.model.entities.deserializers.AlbumsDeserializer
import com.dandy.ugnius.dandy.artist.model.entities.deserializers.ArtistDeserializer
import com.dandy.ugnius.dandy.artist.model.entities.deserializers.TracksDeserializer
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [UtilitiesModule::class])
class NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(authenticationPreferences: SharedPreferences?) = with(OkHttpClient().newBuilder()) {
        val accessToken = authenticationPreferences?.getString("access_token", "") ?: ""
        addInterceptor { chain ->
            val originalRequest = chain.request()
            val builder = originalRequest.newBuilder().header("Authorization", " Bearer $accessToken")
            val modifiedRequest = builder.build()
            chain.proceed(modifiedRequest)
        }
        build()
    }

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory {
        val trackListType = TypeToken.getParameterized(List::class.java, Track::class.java).type
        val albumsListType = TypeToken.getParameterized(List::class.java, Album::class.java).type
        val artistsListType = TypeToken.getParameterized(List::class.java, Artist::class.java).type
        val builder = GsonBuilder().apply {
            setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            registerTypeAdapter(trackListType, TracksDeserializer())
            registerTypeAdapter(albumsListType, AlbumsDeserializer())
            registerTypeAdapter(artistsListType, ArtistDeserializer())
        }
        return GsonConverterFactory.create(builder.create())
    }

    @Singleton
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

    @Singleton
    @Provides
    fun provideArtistClient(retrofit: Retrofit) = retrofit.create(APIClient::class.java)

}