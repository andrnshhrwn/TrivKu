package com.pab.trivku.data

import androidx.lifecycle.LiveData
import com.pab.trivku.data.models.Destination
import com.pab.trivku.data.local.DestinationDao

class DestinationRepository(private val destinationDao: DestinationDao) {
    val allDestinations: LiveData<List<Destination>> = destinationDao.getAllDestinations()

    fun getDestinationsFromDb() = destinationDao.getAllDestinations()

    fun searchDestinations(query: String): LiveData<List<Destination>> {
        return destinationDao.searchDestinations(query)
    }
}