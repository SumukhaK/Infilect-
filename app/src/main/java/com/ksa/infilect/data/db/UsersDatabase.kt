package com.ksa.infilect.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [UsersEntity::class],version = 1,exportSchema = false)
@TypeConverters(UsersTypeConverter::class)
abstract class UsersDatabase :RoomDatabase(){

    abstract fun usersDao(): UsersDao
}