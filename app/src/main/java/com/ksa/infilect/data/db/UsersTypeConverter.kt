package com.ksa.infilect.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ksa.infilect.models.RandomUsers

class UsersTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun usersToString(users: RandomUsers):String{
        return gson.toJson(users)
    }

    @TypeConverter
    fun stringToUsers(data : String):RandomUsers{

        val listType = object : TypeToken<RandomUsers>(){}.type

        return gson.fromJson(data,listType)
    }


}