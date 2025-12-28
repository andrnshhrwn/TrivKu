package com.pab.trivku.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pab.trivku.data.FavoriteRepository
import com.pab.trivku.data.models.Destination
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val repository: FavoriteRepository
) : ViewModel() {

    fun getFavorites(userId: Int): LiveData<List<Destination>> {
        return repository.getFavorites(userId)
    }

    fun toggleFavorite(
        userId: Int,
        destinationId: Int,
        isFavorite: Boolean
    ) {
        viewModelScope.launch {
            repository.toggleFavorite(
                userId,
                destinationId,
                isFavorite
            )
        }
    }
}
