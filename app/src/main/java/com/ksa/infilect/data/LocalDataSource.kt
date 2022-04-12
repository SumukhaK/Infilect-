package com.ksa.infilect.data

import com.ksa.infilect.data.db.UsersDao
import com.ksa.infilect.data.db.UsersEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class LocalDataSource @Inject constructor(
    private val usersDao: UsersDao
){

    fun readUsersDatabase(): Flow<List<UsersEntity>> {
        return usersDao.readAllUsers()
    }


    suspend fun insertUsers(usersEntity: UsersEntity){
        usersDao.insertUsers(usersEntity)
    }

    suspend fun deleteAllUsers(){
        usersDao.deleteAllUsers()
    }

}