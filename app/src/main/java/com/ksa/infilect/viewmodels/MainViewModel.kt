package com.ksa.infilect.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ksa.foody.util.NetworkResult
import com.ksa.infilect.data.Repository
import com.ksa.infilect.models.RandomUsers
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel  @ViewModelInject constructor(
        private val repository: Repository, application: Application
): AndroidViewModel(application) {

    /** ROOM DB **/


    /** RETROFIT **/
    var randomUsersResponse : MutableLiveData<NetworkResult<RandomUsers>> = MutableLiveData()

    fun getRandomUsers(query:String) = viewModelScope.launch {
        getRandomUsersSafeCall(query)
    }

    private suspend fun getRandomUsersSafeCall(query: String) {
        randomUsersResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){

            try{
                val response = repository.remoteDS.getRandomUsersFromApi(query)
                randomUsersResponse.value = handleRandomUsersResponse(response)
                val randomUsers = randomUsersResponse.value!!.data
                /*if(foodRecipe != null){
                    offlineCacheRecipes(foodRecipe)
                }*/
            }catch (e:Exception){
                randomUsersResponse.value = NetworkResult.Error("Users Not found !..")
            }
        }else{
            randomUsersResponse.value = NetworkResult.Error("No Internet Connection.")
        }

    }

    private fun handleRandomUsersResponse(response: Response<RandomUsers>): NetworkResult<RandomUsers>? {

        when{
            response.message().toString().contains("timeout") ->{
                return NetworkResult.Error("Timeout")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recipe Not Found..")
            }
            response.isSuccessful ->{
                val randomUsers = response.body()
                return NetworkResult.Success(randomUsers!!)
            }else ->{
            return NetworkResult.Error(response.message())
        }
        }

    }

    private fun hasInternetConnection():Boolean{

        val connectivityManager = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}