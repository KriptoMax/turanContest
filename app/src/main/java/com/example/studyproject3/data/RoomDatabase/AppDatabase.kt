package com.example.studyproject3.data.RoomDatabase

import androidx.room.RoomDatabase
import androidx.room.Database
import com.example.studyproject3.BaseTasks.Task
import com.example.studyproject3.BaseTasks.SubTask
import com.example.studyproject3.data.TaskDao
import com.example.studyproject3.data.SubTaskDao

// Поднимаем версию до 4, чтобы гарантированно сбросить старую схему
@Database(entities = [Task::class, SubTask::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun subTaskDao(): SubTaskDao
}