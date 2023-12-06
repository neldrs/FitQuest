package com.example.fitquest.ui.nutrition

import com.androidplot.xy.BarFormatter
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYGraphWidget
import com.androidplot.xy.XYPlot
import com.androidplot.xy.XYSeries
import java.text.DecimalFormat


class CalorieChartHelp (private val plot: XYPlot) {
    fun plotCals(weekDays: List<String>, consumedCals: List<Int>) {
        val series: XYSeries = SimpleXYSeries(
            weekDays.mapIndexed { index, _ -> index },
            consumedCals,
            "Calories Consumed"
        )

        // Add the series to the plot
        val seriesFormat = BarFormatter()
        plot.addSeries(series, seriesFormat)

        // Customize the plot
        plot.setTitle("Daily Consumed Calories")
        plot.graph.getLineLabelStyle(XYGraphWidget.Edge.LEFT).format = DecimalFormat("0")
        plot.setRangeLabel("Calories")
        plot.setDomainLabel("Day")

        // Set labels for each bar
        plot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = DecimalFormat("#")

        // Refresh the plot
        plot.redraw()
    }
}

