package com.pab.trivku.data

import com.pab.trivku.data.local.UserDao
import com.pab.trivku.data.models.User

class AuthRepository(private val userDao: UserDao) {

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun addUser(user: User): Long {
        return userDao.addUser(user)
    }

    suspend fun updatePass(newPass: String, userId: Int): Int {
        return userDao.updatePass(newPass, userId)
    }

    suspend fun updateUser(username: String, email: String, userId: Int): Int {
        return userDao.updateUser(username, email, userId)
    }

}