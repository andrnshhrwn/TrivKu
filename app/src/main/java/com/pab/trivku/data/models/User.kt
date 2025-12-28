package com.pab.trivku.data.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_table",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val email: String,
    val password: String
)
