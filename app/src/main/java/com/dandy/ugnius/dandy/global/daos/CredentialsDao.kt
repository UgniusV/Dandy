package com.dandy.ugnius.dandy.global.daos

import android.arch.persistence.room.*
import com.dandy.ugnius.dandy.global.entities.Credentials

@Dao
interface CredentialsDao {

    @Query("SELECT * FROM credentials LIMIT 1")
    fun getCredentials(): Credentials?

    @Query("SELECT * FROM credentials")
    fun getAllCredentials(): List<Credentials>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCredentials(credentials: Credentials)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCredentials(credentials: Credentials)

}