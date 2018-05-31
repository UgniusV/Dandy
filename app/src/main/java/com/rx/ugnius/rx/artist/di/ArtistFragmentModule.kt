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
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ArtistFragmentModule {
    //presenter

//    @Provides
//    fun provideArtistPresenter()

    @Provides
    fun provideOkHttpClient() = with(OkHttpClient().newBuilder()) {
        val accesToken = " Bearer BQCg5P6wswsJbxYO8Umyh18kRgpi_J2cbZKp-bTPCMiEKg1cMuQ3QqfyKf8ogpeQo9TT6FVplXRxQcaiQMxrO02Zg0GtdaJmrJs4qhtJMkAfCDzEorNh-qDOkPE1XoU01sEkbcRxI7cmExGI3LXYpKXEdXkWy9GwZrE5-Lms9I1PBsLypw"
        addInterceptor { chain ->
            val originalRequest = chain.request()
            val builder = originalRequest.newBuilder().header("Authorization", accesToken)
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

}