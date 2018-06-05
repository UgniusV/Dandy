package com.dandy.ugnius.dandy.artist.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.dandy.ugnius.dandy.artist.model.entities.Album
import com.dandy.ugnius.dandy.artist.model.entities.Artist
import com.dandy.ugnius.dandy.artist.model.entities.Track
import com.dandy.ugnius.dandy.artist.model.entities.deserializers.AlbumsDeserializer
import com.dandy.ugnius.dandy.artist.model.entities.deserializers.ArtistDeserializer
import com.dandy.ugnius.dandy.artist.model.entities.deserializers.TracksDeserializer
import dagger.Module
import dagger.Provides
import com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
import com.dandy.ugnius.dandy.artist.model.ArtistClient
import com.dandy.ugnius.dandy.artist.presenter.ArtistPresenter
import com.dandy.ugnius.dandy.artist.view.ArtistView
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ArtistModule(private val artistView: ArtistView, private val context: Context?) {

    //todo look into scopes
    @Provides
    fun provideArtistPresenter(artistClient: ArtistClient) = ArtistPresenter(artistClient, artistView)

    @Provides
    fun provideOkHttpClient(authenticationPreferences: SharedPreferences?) = with(OkHttpClient().newBuilder()) {
        //todo pasidaryti abstractu access token instanca kuri galetume saugoti pvz i realma ir kuris zinotu ar yra valid ar nebe ir pats save pasirefreshintu ar w/e
        val accessToken = authenticationPreferences?.getString("access_token","") ?: ""
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

    @Provides
    fun provideAuthenticationPreferences(): SharedPreferences? {
        return context?.getSharedPreferences("authentication_preferences", Context.MODE_PRIVATE)
    }

}