package com.example.myapplication

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineDataSet


/**
 * Class to style the graph.
 */
class GraphStyle constructor(val context: Context){


    /**
     * Function to style the chart.
     */
    fun styleChart(lineChart: LineChart) = lineChart.apply {
        axisRight.isEnabled = true
        axisLeft.isEnabled = false
        xAxis.apply {
            isGranularityEnabled = true
            granularity = 24f
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            setDrawAxisLine(false)
        }
        setTouchEnabled(true)
        isDragEnabled = true
        description = null
        legend.isEnabled = false
    }

    /**
     * Function to set styling on the graphline.
     */
    fun styleLine(lineDataSet: LineDataSet) = lineDataSet.apply {
        color = ContextCompat.getColor(context, R.color.black)
        setDrawValues(false)
        setDrawHighlightIndicators(true)
        lineWidth = 0.5f
        isHighlightEnabled = true
        setDrawHighlightIndicators(true)
        setDrawCircles(false)
        setDrawFilled(true)
        fillDrawable = ContextCompat.getDrawable(context, R.color.lightGreen)
    }
}