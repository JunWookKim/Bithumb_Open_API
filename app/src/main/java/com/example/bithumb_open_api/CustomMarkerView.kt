package com.example.bithumb_open_api

import android.content.Context
import android.graphics.Canvas
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight


class CustomMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val textPrice : TextView = findViewById(R.id.markerPrice)

    override fun draw(canvas: Canvas?) {
        canvas!!.translate(-(width / 2).toFloat(), -height.toFloat())
        super.draw(canvas)
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        textPrice.text = e?.y?.toBigDecimal()?.toPlainString() + " 원"
        super.refreshContent(e, highlight)
    }
}