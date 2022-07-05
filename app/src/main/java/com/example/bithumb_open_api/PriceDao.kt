package com.example.bithumb_open_api

import androidx.room.*

@Dao
interface PriceDao{
    @Query("SELECT * FROM info_table")
    fun getAll() : List<IntegratedInfo>

    @Query("SELECT * FROM info_table WHERE name = :name")
    fun get(name: String) : IntegratedInfo

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertInfo(IntegratedInfo : IntegratedInfo)

    @Update
    fun updateInfo(IntegratedInfo : IntegratedInfo)

    @Query("DELETE FROM info_table")
    fun deleteAll()


}