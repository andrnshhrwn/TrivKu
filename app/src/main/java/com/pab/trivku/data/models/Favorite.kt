package com.pab.trivku.data.models

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "favorite",
    primaryKeys = ["userId", "destinationId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Destination::class,
            parentColumns = ["id"],
            childColumns = ["destinationId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Favorite(
    val userId: Int,
    val destinationId: Int
)