package com.pab.trivku.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pab.trivku.data.DestinationRepository
import java.lang.IllegalArgumentException

// Factory ini menerima Repository sebagai parameter
class DestinationViewModelFactory(private val repository: DestinationRepository) : ViewModelProvider.Factory {

    // Fungsi yang dipanggil oleh sistem Android untuk membuat instance ViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Memeriksa apakah modelClass adalah DestinationsViewModel
        if (modelClass.isAssignableFrom(DestinationsViewModel::class.java)) {
            // Jika benar, buat dan kembalikan instance DestinationsViewModel
            @Suppress("UNCHECKED_CAST")
            return DestinationsViewModel(repository) as T
        }
        // Jika modelClass bukan yang diharapkan, lempar exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}