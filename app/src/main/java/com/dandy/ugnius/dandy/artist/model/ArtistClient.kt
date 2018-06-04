package com.dandy.ugnius.dandy.artist.model

import com.dandy.ugnius.dandy.artist.model.entities.Album
import com.dandy.ugnius.dandy.artist.model.entities.Artist
import com.dandy.ugnius.dandy.artist.model.entities.Track
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArtistClient {

    @GET("v1/artists/{artistId}")
    fun getArtist(@Path("artistId") artistId: String): Single<Artist>

    @GET("v1/artists/{artistId}/top-tracks")
    fun getArtistTopTracks(@Path("artistId") artistId: String, @Query("country") country: String): Single<List<Track>>

    @GET("v1/artists/{artistId}/albums")
    fun getArtistAlbums(@Path("artistId") artistId: String): Observable<List<Album>>

    @GET("v1/albums/{albumId}/tracks")
    fun getAlbumsTracks(@Path("albumId") albumId: String): Observable<List<Track>>

    @GET("v1/artists/{artistId}/related-artists")
    fun getArtistRelatedArtists(@Path("artistId") artistId: String): Single<List<Artist>>

    @GET("v1/tracks/{trackId}")
    fun getFullTrack(@Path("trackId") trackId: String): Single<Track>

}
