package com.dandy.ugnius.dandy.player.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.spotify.sdk.android.player.SpotifyPlayer
import javax.inject.Inject

class ConnectionStateReceiver : BroadcastReceiver() {

    @Inject lateinit var player: SpotifyPlayer

    //this will be used later on
    override fun onReceive(context: Context, intent: Intent?) {
//        (context as App).mainComponent?.inject(this)
//        val network = (context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
//        val connectivity = if (network != null && network.isConnected) {
//            Connectivity.fromNetworkType(network.type)
//        } else {
//            OFFLINE
//        }
//        player.setConnectivityStatus(null, connectivity)
    }
}