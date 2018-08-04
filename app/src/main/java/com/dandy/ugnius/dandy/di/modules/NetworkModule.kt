package com.dandy.ugnius.dandy.di.modules

import com.dandy.ugnius.dandy.global.clients.APIClient
import com.dandy.ugnius.dandy.global.deserializers.*
import com.dandy.ugnius.dandy.global.entities.Album
import com.dandy.ugnius.dandy.global.entities.Artist
import com.dandy.ugnius.dandy.global.entities.Track
import com.dandy.ugnius.dandy.global.interceptors.AuthorizationInterceptor
import com.dandy.ugnius.dandy.login.model.clients.AuthenticationClient
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

@Module(includes = [GeneralModule::class])
class NetworkModule {

    @Provides
    fun provideOkHttpClient(interceptor: AuthorizationInterceptor): OkHttpClient {
        return OkHttpClient().newBuilder().apply { addInterceptor(interceptor) }.build()
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
    ): Retrofit = with(Retrofit.Builder()) {
        baseUrl("https://api.spotify.com")
        addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        addConverterFactory(gsonConverterFactory)
        client(okHttpClient)
        build()
    }

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
    fun provideClient(retrofit: Retrofit): APIClient = retrofit.create(APIClient::class.java)

    @Provides
    fun provideAuthClient(@Named("authentication") retrofit: Retrofit): AuthenticationClient {
        return retrofit.create(AuthenticationClient::class.java)
    }

}