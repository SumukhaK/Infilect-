package com.ksa.infilect.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(usersEntity: UsersEntity)


    @Query("SELECT * FROM users ORDER BY id ASC")
    fun readAllUsers(): Flow<List<UsersEntity>>


    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

}