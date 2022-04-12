package com.ksa.infilect.data

import com.ksa.infilect.models.RandomUsers
import com.ksa.infilect.network.RandomUsersApi
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val randomUsersApi: RandomUsersApi
){

    suspend fun getRandomUsersFromApi(query:String): Response<RandomUsers> {
        return randomUsersApi.getRandomUsers(query)
    }
}