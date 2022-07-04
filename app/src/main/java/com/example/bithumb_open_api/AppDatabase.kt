package com.example.bithumb_open_api

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Info::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun priceDao() : PriceDao

}