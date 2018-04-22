package com.rx.ugnius.rx.api

import com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rx.ugnius.rx.api.entities.Album
import com.rx.ugnius.rx.api.entities.Artist
import com.rx.ugnius.rx.api.entities.Track
import com.rx.ugnius.rx.api.entities.deserializers.AlbumsDeserializer
import com.rx.ugnius.rx.api.entities.deserializers.ArtistDeserializer
import com.rx.ugnius.rx.api.entities.deserializers.TracksDeserializer
import com.rx.ugnius.rx.artist.TracksAdapter
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConfigurator private constructor() {

    companion object {


        private const val ACCESS_TOKEN = " Bearer BQDfOtjgZ7fmk0bRavGWN8hppepomD3kztcTcxZ7yLOl0HTF9V-MrSDGpbYuBWKR4RJym9hguCMQrRCIsceLwFHYEEK-t70brhqQ6Dj9djZcErB507sKnk-AGzobxTCEQ4b0zEBgb0BdVy5jeFYpyFBJW8GC"

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
        //sonBuilder.registerTypeAdapter(ArrayList<Track>()::class.java, TrackMapper.TracksListDeserializer())

        private fun createGsonConverterFactory(): GsonConverterFactory {
            val trackListType = TypeToken.getParameterized(List::class.java, Track::class.java).type
            val albumsListType = TypeToken.getParameterized(List::class.java, Album::class.java).type
            val artistsListType = TypeToken.getParameterized(List::class.java, Artist::class.java).type
            val builder = GsonBuilder()
            builder.setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
            builder.registerTypeAdapter(trackListType, TracksDeserializer())
            builder.registerTypeAdapter(albumsListType, AlbumsDeserializer())
//            builder.registerTypeAdapter(artistsListType, ArtistDeserializer())
            return GsonConverterFactory.create(builder.create())
        }
    }

}