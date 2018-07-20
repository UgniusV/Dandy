package com.dandy.ugnius.dandy.model.clients

import com.dandy.ugnius.dandy.model.entities.*
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface APIClient {

    @GET("v1/artists/{artistId}")
    fun getArtist(@Path("artistId") artistId: String): Single<Artist>

    @GET("v1/artists/{artistId}/top-tracks")
    fun getArtistTopTracks(@Path("artistId") artistId: String, @Query("country") country: String): Observable<List<Track>>

    @GET("v1/artists/{artistId}/albums")
    fun getArtistAlbums(
        @Path("artistId") artistId: String,
        @Query("include_groups") groups: String
    ): Observable<List<Album>>

    @GET("v1/albums/{albumId}/tracks")
    fun getAlbumsTracks(@Path("albumId") albumId: String): Observable<List<Track>>

    @GET("v1/artists/{artistId}/related-artists")
    fun getSimilarArtists(@Path("artistId") artistId: String): Observable<List<Artist>>

    @GET("v1/tracks/{trackId}")
    fun getFullTrack(@Path("trackId") trackId: String): Single<Track>

}
