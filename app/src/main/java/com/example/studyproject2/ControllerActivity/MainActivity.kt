package com.example.studyproject3.ControllerActivity

import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.studyproject3.ControllerActivity.TaskAdapter
import com.example.studyproject3.BaseTasks.Task
import com.example.studyproject3.data.RoomDatabase.AppDatabase
import com.example.studyproject3.HandlerController.BackTransitionController
import com.example.studyproject3.HandlerController.TransitionController
import com.example.studyproject3.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import com.example.studyproject3.R


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding for MainActivity must not be null") //ActivityLearnWordBinding

    private lateinit var taskAdapter: TaskAdapter

    //Кустарная инициализация базы
    val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "my_app_db"
        )
            .fallbackToDestructiveMigration()
            .build()
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
        setupRecyclerView()
        loadTasks()
        binding.btn1Create.setOnClickListener {
            val dialog = AddTaskDialog{ newTaskTitle ->
                //Код сработает если нажать.
                saveTask(newTaskTitle)
                Toast.makeText(this, "Добавлено: $newTaskTitle", Toast.LENGTH_SHORT).show()

                //Добавление в RecyclerView
            }
            dialog.show(supportFragmentManager, "AddTaskDialog")
        }
        binding.btnProfile.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.top_app_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.creator_nickname) {
                    Toast.makeText(this, "Это ваш профиль", Toast.LENGTH_SHORT).show()
                    true
                } else false
            }
            popup.show()
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            mutableListOf(),
            onDelete = { task ->
                lifecycleScope.launch {
                    db.taskDao().deleteTask(task)
                    loadTasks()
                }
            },
            onEdit = { task ->
                // Открываем диалог для редактирования существующей задачи
                val dialog = AddTaskDialog { updatedTitle ->
                    updateTask(task, updatedTitle)
                }
                dialog.show(supportFragmentManager, "EditTaskDialog")
            }
        )
        binding.recV.layoutManager = LinearLayoutManager(this)
        binding.recV.adapter = taskAdapter
    }
    private fun loadTasks(){
        lifecycleScope.launch{
            val tasks = db.taskDao().getAllTasks()
            taskAdapter.updateData(tasks)
        }
    }
    private fun saveTask(title: String){
        lifecycleScope.launch{
            val newTask = Task(title = title)
            db.taskDao().insertTask(newTask)
            loadTasks()
        }
    }
    private fun updateTask(task: Task, newTitle: String) {
        lifecycleScope.launch {
            // Создаем копию задачи с новым заголовком (id остается прежним)
            val updatedTask = task.copy(title = newTitle)
            db.taskDao().updateTask(updatedTask)
            loadTasks() // Обновляем список на экране
            Toast.makeText(this@MainActivity, "Задача обновлена", Toast.LENGTH_SHORT).show()
        }
    }
}
//lifecycleScope.launch {
//val userDao = db.userDao()

//Имитация регистрации Создателя
//val creator = User(username = "Boss", password = "qwerty", role = "CREATOR")
//userDao.registerUser(creator)

//Проверка, сохранился ли он
//val users = userDao.getAllUsers()
//binding.btn1Create.setOnClickListener {
//binding.tvTaskDeducation.text = "Пользователи в базе данных ${users} "
//}
//}