package com.example.bithumb_open_api

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "info_table")
data class IntegratedInfo(
    @PrimaryKey @ColumnInfo(name = "name")
    val name : String,
    @ColumnInfo(name = "opening_price")
    val opening : String?,
    @ColumnInfo(name = "closing_price")
    val closing : String?,
    @ColumnInfo(name = "min_price")
    val min : String?,
    @ColumnInfo(name = "max_price")
    val max : String?,
    @ColumnInfo(name = "units_traded")
    val unitTraded: String?,
    @ColumnInfo(name = "acc_trade_value")
    val tradedValue: String?,
    @ColumnInfo(name = "units_traded_24H")
    val unitTraded24H: String?,
    @ColumnInfo(name = "acc_trade_value_24H")
    val tradeValue24H: String?,
    @ColumnInfo(name = "fluctate_24H")
    val fluctate24H : String?,
    @ColumnInfo(name = "fluctate_rate_24H")
    val fluctateRate24H: String?,
    @ColumnInfo(name = "prev_closing_price")
    val prevClosing: String?,
    @ColumnInfo(name = "date")
    val date: Long?
)
