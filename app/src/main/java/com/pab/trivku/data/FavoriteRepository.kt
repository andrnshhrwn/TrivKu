package com.pab.trivku.data

import androidx.lifecycle.LiveData
import com.pab.trivku.data.local.DestinationDao
import com.pab.trivku.data.local.FavoriteDao
import com.pab.trivku.data.models.Destination
import com.pab.trivku.data.models.Favorite

class FavoriteRepository(
    private val favoriteDao: FavoriteDao,
    private val destinationDao: DestinationDao
) {

    fun getFavorites(userId: Int): LiveData<List<Destination>> {
        return favoriteDao.getFavoriteDestinations(userId)
    }

    suspend fun toggleFavorite(
        userId: Int,
        destinationId: Int,
        isFavorite: Boolean
    ) {
        if (isFavorite) {
            favoriteDao.removeFavorite(
                Favorite(userId, destinationId)
            )
            destinationDao.updateFav(isFavorite, destinationId)
        } else {
            favoriteDao.addFavorite(
                Favorite(userId, destinationId)
            )
        }
    }
}