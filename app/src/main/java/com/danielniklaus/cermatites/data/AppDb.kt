package com.danielniklaus.cermatites.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.danielniklaus.cermatites.usersearch.vo.User
import com.danielniklaus.cermatites.usersearch.vo.UserDao
import com.danielniklaus.cermatites.usersearch.vo.UserSearchResult

@Database(
    entities = [
        User::class,
        UserSearchResult::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {

    abstract fun userDao(): UserDao

}