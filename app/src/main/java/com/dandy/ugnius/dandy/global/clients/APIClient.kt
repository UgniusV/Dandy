package com.dandy.ugnius.dandy.global.clients

import com.dandy.ugnius.dandy.global.entities.Album
import com.dandy.ugnius.dandy.global.entities.Artist
import com.dandy.ugnius.dandy.global.entities.Track
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.*

interface APIClient {

    @GET("v1/artists/{artistId}")
    fun getArtist(@Path("artistId") artistId: String): Single<Artist>

    @GET("v1/artists/{artistId}/top-tracks")
    fun getArtistTopTracks(
        @Path("artistId") artistId: String,
        @Query("country") country: String
    ): Observable<List<Track>>

    @GET("v1/artists/{artistId}/albums")
    fun getArtistAlbums(
        @Path("artistId") artistId: String,
        @Query("include_groups") groups: String
    ): Observable<List<Album>>

    @GET("v1/albums/{albumId}/tracks")
    fun getAlbumsTracks(@Path("albumId") albumId: String): Observable<List<Track>>

    @GET("v1/artists/{artistId}/related-artists")
    fun getSimilarArtists(@Path("artistId") artistId: String): Observable<List<Artist>>

    @GET("v1/me/tracks")
    fun getSavedTracks(@Query("offset") offset: Int, @Query("limit") limit: Int): Single<List<Track>>

    @GET("v1/me/albums")
    fun getSavedAlbums(): Observable<List<Album>>

    @GET("v1/me/following")
    fun getFollowingArtists(@Query("type") type: String): Single<List<Artist>>

    @GET("v1/me/top/artists")
    fun getMyTopArtists(): Single<List<Artist>>

    @GET("v1/me/top/tracks")
    fun getMyTopTracks(): Single<List<Track>>

}