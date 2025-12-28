package com.pab.trivku.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pab.trivku.data.models.Destination
import com.pab.trivku.data.models.Favorite

@Dao
interface FavoriteDao {

    @Query("""
        SELECT d.* 
        FROM destinations d
        INNER JOIN favorite f 
            ON d.id = f.destinationId
        WHERE f.userId = :userId
    """)
    fun getFavoriteDestinations(userId: Int): LiveData<List<Destination>>

    // TAMBAH FAVORITE
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: Favorite)

    // HAPUS FAVORITE
    @Delete
    suspend fun removeFavorite(favorite: Favorite)

    // OPTIONAL: kalau mau cek status (dipakai di adapter)
    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM favorite
            WHERE userId = :userId
            AND destinationId = :destinationId
        )
    """)
    suspend fun isFavorite(userId: Int, destinationId: Int): Boolean
}