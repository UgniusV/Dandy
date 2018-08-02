package com.dandy.ugnius.dandy.model.clients

import com.dandy.ugnius.dandy.model.entities.*
import io.reactivex.Maybe
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

    @GET("v1/albums/{albumId}/tracks")
    fun getAlbumsTracks(
        @Path("albumId") albumId: String,
        @Header("Authorization") accessToken: String
    ): Observable<List<Track>>

    @GET("v1/artists/{artistId}/related-artists")
    fun getSimilarArtists(@Path("artistId") artistId: String): Observable<List<Artist>>

    @GET("v1/me/tracks")
    fun getSavedTracks(@Query("offset") offset: Int, @Query("limit") limit: Int): Single<List<Track>>

    @GET("v1/me/albums")
    fun getSavedAlbums(): Observable<List<Album>>

    @GET("v1/me/following")
    fun getFollowingArtists(@Query("type") type: String): Single<List<Artist>>

    @DELETE("v1/me/tracks")
    fun deleteTrack(@Query("ids") ids: String): Call<Unit>

}
