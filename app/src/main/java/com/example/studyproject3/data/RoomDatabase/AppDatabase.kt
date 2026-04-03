package com.example.studyproject3.data.RoomDatabase

import androidx.room.RoomDatabase
import androidx.room.Database
import com.example.studyproject3.BaseTasks.Task
import com.example.studyproject3.data.TaskDao

@Database(entities = [Task::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}