package com.example.bithumb_open_api

import androidx.room.*

@Dao
interface PriceDao{
    @Query("SELECT * FROM info_table")
    fun getAll() : List<IntegratedInfo>

    @Query("SELECT * FROM info_table WHERE name like '%'||:name||'%' and date = :date")
    fun getLatestValueByName(name: String, date: Long?) : List<IntegratedInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInfo(IntegratedInfo : IntegratedInfo)

    @Update
    fun updateInfo(IntegratedInfo : IntegratedInfo)

    @Query("DELETE FROM info_table")
    fun deleteAll()

    @Query("SELECT max(date) FROM info_table")
    fun getMaxDate() : Long?

    @Query("SELECT name, closing_price, date FROM info_table WHERE name = 'BTC'")
    fun getBTC() : List<BTC>

    @Query("DELETE FROM info_table WHERE date = :date")
    fun deleteMaxDate(date : Long?)


}