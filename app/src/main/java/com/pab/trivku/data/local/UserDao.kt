package com.pab.trivku.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pab.trivku.data.models.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User): Long

    @Query("UPDATE user_table SET password = :newPass WHERE id = :userId")
    suspend fun updatePass(newPass: String, userId: Int): Int

    @Query("UPDATE user_table SET username = :username, email = :email WHERE id = :userId")
    suspend fun updateUser(username: String, email: String, userId: Int): Int

    @Query("SELECT * FROM user_table WHERE email = :findEmail LIMIT 1")
    suspend fun getUserByEmail(findEmail: String): User?

}