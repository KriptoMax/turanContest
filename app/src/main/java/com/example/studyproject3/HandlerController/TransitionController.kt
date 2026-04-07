package com.example.studyproject3.HandlerController

import android.content.Context
import android.content.Intent
import com.example.studyproject3.ControllerActivity.MainActivity
import com.example.studyproject3.ControllerActivity.OneTaskActivity
import com.example.studyproject3.databinding.ActivityMainBinding
import com.example.studyproject3.databinding.ActivityOneTaskBinding


class TransitionController(private val binding: ActivityMainBinding,
                           private val context: Context) {
    fun oneTask(){
        with(binding){
            tvTaskVault.setOnClickListener {
                controllerOneTaskActivity()
            }
        }
    }


    private fun controllerOneTaskActivity(){
        val intent = Intent(context, OneTaskActivity::class.java)
        context.startActivity(intent)
    }

}