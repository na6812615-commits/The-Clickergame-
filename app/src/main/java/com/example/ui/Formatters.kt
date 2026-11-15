package com.example.ui

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.roundToLong

object Formatters {
    private val symbols = DecimalFormatSymbols(Locale.US)
    private val intFormat = DecimalFormat("#,##0", symbols)
    private val decimal2Format = DecimalFormat("#,##0.##", symbols)
    private val fixed2Format = DecimalFormat("#,##0.00", symbols)

    fun formatPoints(value: Double): String = intFormat.format(value.roundToLong().coerceAtLeast(0L))

    fun formatLong(value: Long): String = intFormat.format(value)

    fun formatDecimal(value: Double): String = decimal2Format.format(value)

    fun formatDollars(value: Double): String {
        val rounded = (value * 100.0).roundToLong() / 100.0
        return if (rounded % 1.0 == 0.0) {
            "$${intFormat.format(rounded.toLong())}"
        } else {
            "$${fixed2Format.format(rounded)}"
        }
    }

    fun formatSeconds(value: Double): String {
        val formatted = DecimalFormat("0.###", symbols).format(value)
        return "${formatted}s"
    }

    fun formatMultiplier(value: Double): String {
        val rounded = (value * 100.0).roundToLong() / 100.0
        return "${decimal2Format.format(rounded)}x"
    }
}
