package com.example.studyproject3.di

import androidx.room.Room
import com.example.studyproject3.data.RoomDatabase.AppDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    // Единственный экземпляр базы данных Room (оставим для кеша, если нужно)
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "my_app_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    // Дао для задач
    single { get<AppDatabase>().taskDao() }

    // Дао для подзадач
    single { get<AppDatabase>().subTaskDao() }

    // Firebase Firestore
    single { Firebase.firestore }
}