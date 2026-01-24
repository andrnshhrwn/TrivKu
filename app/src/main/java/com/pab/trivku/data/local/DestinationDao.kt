package com.pab.trivku.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pab.trivku.data.models.Destination

@Dao
interface DestinationDao {

    @Query("SELECT * FROM destinations ORDER BY id ASC")
    fun getAllDestinations(): LiveData<List<Destination>>

    @Query("SELECT * FROM destinations WHERE category = :category")
    fun getDestinationsByCategory(category: String): LiveData<List<Destination>>

    @Query("SELECT * FROM destinations WHERE name LIKE :searchQuery OR location LIKE :searchQuery")
    fun searchDestinations(searchQuery: String): LiveData<List<Destination>>

    @Insert
    suspend fun insertAll(destinations: List<Destination>)

    @Query("UPDATE destinations SET isFavorite= :isFav WHERE id= :idDestination")
    suspend fun updateFav(isFav: Boolean, idDestination: Int)
}