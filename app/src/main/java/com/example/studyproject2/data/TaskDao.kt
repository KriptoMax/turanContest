package com.example.studyproject3.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.studyproject3.BaseTasks.Task

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: Task)

    //Авторизация: ищем пользователя по логину и паролю
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    suspend fun getAllTasks(): List<Task>

    // Получить всех для проверки
    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)
}