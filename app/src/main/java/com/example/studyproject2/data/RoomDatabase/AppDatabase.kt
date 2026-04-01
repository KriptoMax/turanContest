package com.example.studyproject3.data.RoomDatabase

import androidx.room.RoomDatabase
import androidx.room.Database
import com.example.studyproject3.BaseUsers.User
import com.example.studyproject3.data.UserDao

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}