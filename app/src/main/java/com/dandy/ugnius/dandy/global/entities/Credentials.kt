package com.dandy.ugnius.dandy.global.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class Credentials(@PrimaryKey val accessToken: String, val refreshToken: String)