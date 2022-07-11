package com.example.bithumb_open_api

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "info_table", primaryKeys = ["name", "date"])
data class IntegratedInfo(
    @ColumnInfo(name = "name")
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
    val date: Long
)

@Parcelize
data class SimpleInfo(
    val name : String,
    val closing_price : String,
    val date : Long
) : Parcelable