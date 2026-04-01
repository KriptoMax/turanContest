package com.example.studyproject3.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.studyproject3.BaseUsers.User

@Dao
interface UserDao {

    @Insert
    suspend fun registerUser(user: User)

    //Авторизация: ищем пользователя по логину и паролю
    @Query("SELECT * FROM users WHERE username = :login AND password = :pass LIMIT 1")
    suspend fun login(login: String, pass: String): User?

    // Получить всех для проверки
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}