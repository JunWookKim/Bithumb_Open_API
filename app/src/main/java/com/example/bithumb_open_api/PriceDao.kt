package com.example.bithumb_open_api

import androidx.room.*

@Dao
interface PriceDao{
    @Query("SELECT * FROM info_table")
    fun getAll() : List<Info>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertInfo(Info : Info)

    @Update
    fun updateInfo(Info : Info)


}