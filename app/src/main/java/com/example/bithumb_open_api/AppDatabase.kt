package com.example.bithumb_open_api

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [IntegratedInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun priceDao() : PriceDao

}