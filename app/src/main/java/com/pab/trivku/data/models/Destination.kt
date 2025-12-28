package com.pab.trivku.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "destinations")
data class Destination(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val location: String,
    val imageResource: Int,
    val describe: String,
    var isFavorite: Boolean = false
): Parcelable