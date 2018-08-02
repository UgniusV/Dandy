package com.dandy.ugnius.dandy.di.modules

import android.content.SharedPreferences
import com.dandy.ugnius.dandy.model.clients.APIClient
import com.dandy.ugnius.dandy.model.clients.AuthClient
import com.dandy.ugnius.dandy.model.deserializers.*
import com.dandy.ugnius.dandy.model.entities.Album
import com.dandy.ugnius.dandy.model.entities.Artist
import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.model.entities.PlaybackInfo
import com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@Module(includes = [UtilitiesModule::class])
class NetworkModule {

    @Provides
    fun provideOkHttpClient(authenticationPreferences: SharedPreferences?) = with(OkHttpClient().newBuilder()) {
        val accessToken = authenticationPreferences?.getString("accessToken", "") ?: ""
        //use real access token
        addInterceptor { chain ->
            val originalRequest = chain.request()
            val builder = originalRequest.newBuilder().header("Authorization", " Bearer $accessToken")
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
        val builder = GsonBuilder().apply {
            setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
            registerTypeAdapter(Track::class.java, TrackDeserializer())
            registerTypeAdapter(Artist::class.java, ArtistDeserializer())
            registerTypeAdapter(PlaybackInfo::class.java, PlaybackInfoDeserializer())
            registerTypeAdapter(trackListType, TracksDeserializer())
            registerTypeAdapter(albumsListType, AlbumsDeserializer())
            registerTypeAdapter(artistsListType, ArtistsDeserializer())
        }
        return GsonConverterFactory.create(builder.create())
    }

    @Provides
    @Named("authentication")
    fun provideAuthGsonConverterFactory(): GsonConverterFactory {
        val builder = GsonBuilder().apply { setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES) }
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
    fun provideClient(retrofit: Retrofit) = retrofit.create(APIClient::class.java)

    @Provides
    @Named("authentication")
    fun provideAuthRetrofit(@Named("authentication") gsonConverterFactory: GsonConverterFactory): Retrofit {
        return with(Retrofit.Builder()) {
            baseUrl("https://dry-mesa-35155.herokuapp.com/")
            addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            addConverterFactory(gsonConverterFactory)
            client(OkHttpClient().newBuilder().build())
            build()
        }
    }

    @Provides
    fun provideAuthClient(@Named("authentication") retrofit: Retrofit): AuthClient {
        return retrofit.create(AuthClient::class.java)
    }

}