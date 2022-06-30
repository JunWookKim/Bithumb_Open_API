package com.example.bithumb_open_api

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class ResponseModel(
    @SerializedName("status")
    var status : String,
    @SerializedName("data")
    var data : Object
)

data class InfoModel(
    @SerializedName("opening_price")
    var opening : String,
    @SerializedName("closing_price")
    var closing : String,
    @SerializedName("min_price")
    var min : String,
    @SerializedName("max_price")
    var max : String,
    @SerializedName("units_traded")
    var unitTraded : String,
    @SerializedName("acc_trade_value")
    var tradedValue : String,
    @SerializedName("prev_closing_price")
    var prevClosing : String,
    @SerializedName("units_traded_24H")
    var unitTraded24H : String,
    @SerializedName("acc_trade_value_24H")
    var tradeValue24H : String,
    @SerializedName("fluctate_24H")
    var fluctate24H : String,
    @SerializedName("fluctate_rate_24H")
    var fluctateRate24H : String
)
