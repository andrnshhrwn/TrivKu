package com.pab.trivku.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.pab.trivku.data.DestinationRepository
import com.pab.trivku.data.models.Destination
import kotlinx.coroutines.launch

class DestinationsViewModel(private val repository: DestinationRepository) : ViewModel() {

    // Trigger manual
    private val refreshTrigger = MutableLiveData<Boolean>()

    init {
        // Trigger pertama kali agar data muncul saat aplikasi dibuka
        triggerRefresh()
    }

    val allDestinations: LiveData<List<Destination>> = refreshTrigger.switchMap {
        repository.getDestinationsFromDb()
    }

    // Fungsi untuk memicu ambil ulang
    fun triggerRefresh() {
        refreshTrigger.value = true
    }

    fun searchDestinations(query: String): LiveData<List<Destination>> {
        return repository.searchDestinations(query)
    }
}