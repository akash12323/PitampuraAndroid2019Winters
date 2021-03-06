package com.puldroid.roomdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * @author aggarwalpulkit596
 */
@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User) :Long

    @Insert
    fun insertAllUser(users: List<User>)

    @Query("Select * From User")
    fun getAllUsers(): LiveData<List<User>>

    @Query("Select * From User where age > :elder")
    fun getAllUser(elder:Int):List<User>

    @Delete
    fun deleteUser(user: User)
}