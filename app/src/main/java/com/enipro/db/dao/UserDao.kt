package com.enipro.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

import com.enipro.data.remote.model.User

/**
 * Data access object used to retrieve user information from the database.
 */

@Dao
interface UserDao {

    @get:Query("SELECT * FROM users")
    val users: List<User>

    @Query("SELECT * from users where _id = :user_id")
    fun getUser(user_id: String): User

    @Query("SELECT * FROM users where active = :active")
    fun getActiveUser(active: Boolean): User

    @Update
    fun updateUser(user: User)

    @Insert
    fun insertUser(user: User)

    @Delete
    fun deleteUser(user: User)
}