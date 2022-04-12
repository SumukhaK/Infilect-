package com.ksa.infilect.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ksa.infilect.models.RandomUsers
import com.ksa.infilect.util.Constants

@Entity(tableName = Constants.USERS_TABLE)
class UsersEntity (var users: RandomUsers) {

    @PrimaryKey(autoGenerate = false)
    var id:Int = 0


}