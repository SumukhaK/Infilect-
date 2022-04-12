package com.ksa.infilect.data

import android.content.Context
import androidx.room.Room
import com.ksa.infilect.data.db.UsersDatabase
import com.ksa.infilect.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
            Room.databaseBuilder(context, UsersDatabase::class.java, Constants.DB_NAME).build()

    @Singleton
    @Provides
    fun provideDao(database: UsersDatabase) = database.usersDao()

}