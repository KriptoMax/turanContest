package com.example.studyproject3.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.studyproject3.BaseTasks.SubTask
import com.example.studyproject3.BaseTasks.Task


@Dao
interface SubTaskDao {

    @Insert
    suspend fun insertSubTask(subTask: SubTask)

    //Авторизация: ищем пользователя по логину и паролю
    @Query("SELECT * FROM subtasks ORDER BY id DESC")
    suspend fun getAllSubTasks(): List<SubTask>

    // Получить всех для проверки
    @Delete
    suspend fun deleteSubTask(subTask: SubTask)

    @Update
    suspend fun updateSubTask(subTask: SubTask)

}