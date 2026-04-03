package com.example.studyproject3.ControllerActivity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.studyproject3.HandlerController.BackTransitionController
import com.example.studyproject3.HandlerController.TransitionController
import com.example.studyproject3.databinding.ActivityOneTaskBinding

class OneTaskActivity : AppCompatActivity() {

    private var _binding: ActivityOneTaskBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding for OneTaskActivity must not be null")

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        _binding = ActivityOneTaskBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val controller = BackTransitionController(binding, this)

        controller.oneTask()
        controller.backMain()

    }

}