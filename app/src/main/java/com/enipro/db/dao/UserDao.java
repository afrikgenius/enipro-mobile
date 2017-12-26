package com.enipro.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.enipro.data.remote.model.User;

import java.util.List;

/**
 * Data access object used to retrieve user information from the database.
 */

@Dao
public interface UserDao {

    @Query("SELECT * from users where _id = :user_id")
    User getUser(String user_id);

    @Query("SELECT * FROM users")
    List<User> getUsers();

    @Query("SELECT * FROM users where active = :active")
    User getActiveUser(boolean active);

    @Insert
    void insertUser(User user);

    @Delete
    void deleteUser(User user);
}