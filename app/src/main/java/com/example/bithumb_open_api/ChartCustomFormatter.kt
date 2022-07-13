package com.example.bithumb_open_api

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class ChartCustomFormatter(timestamp : MutableList<String?>) : ValueFormatter() {
    private val timestamp = timestamp.toTypedArray()

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return timestamp.getOrNull(value.toInt()) ?: value.toString()
    }
}
