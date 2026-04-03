package com.example.studyproject3.HandlerController

import android.content.Context
import android.content.Intent
import com.example.studyproject3.ControllerActivity.MainActivity
import com.example.studyproject3.ControllerActivity.OneTaskActivity
import com.example.studyproject3.databinding.ActivityOneTaskBinding


class BackTransitionController(private val binding: ActivityOneTaskBinding, private val context: Context) {
    fun oneTask(){
        with(binding){
            btn1Transit.setOnClickListener {
                controllerOneTaskActivity()
            }
        }
    }


    private fun controllerOneTaskActivity(){
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }

    //Единая функция для возращения на главный экран
    fun backMain(){
        with(binding){
            btn1Transit.setOnClickListener {
                controllerTransitActivity()
            }
        }
    }


    private fun controllerTransitActivity(){
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }

}