package com.example.studyproject3.ControllerActivity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.studyproject3.BaseUsers.User
import com.example.studyproject3.data.RoomDatabase.AppDatabase
import com.example.studyproject3.HandlerController.BackTransitionController
import com.example.studyproject3.HandlerController.TransitionController
import com.example.studyproject3.databinding.ActivityMainBinding
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding for MainActivity must not be null") //ActivityLearnWordBinding

    //Кустарная инициализация базы
    val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "my_app_db"
        ).build()
    }
    //Граница кустарной базы

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //setOnclickListener()
        val controller = TransitionController(binding, this)
        controller.oneTask()
        lifecycleScope.launch {
            val userDao = db.userDao()

            //Имитация регистрации Создателя
            val creator = User(username = "Boss", password = "qwerty", role = "CREATOR")
            userDao.registerUser(creator)

            //Проверка, сохранился ли он
            val users = userDao.getAllUsers()
            binding.btn1Create.setOnClickListener {
                binding.tvTaskDeducation.text = "Пользователи в базе данных ${users} "
            }
        }
    }
}