package com.mhy.landrestoration.util

import org.junit.Assert.assertEquals
import org.junit.Test

internal class CoordinateUtilTest {

    @Test
    fun twd97ToLatLng() {
        val point = CoordinateUtil.twd97ToLatLng(250000.0, 2655023.12512037)
        assertEquals("誤差過大", point.longitude(), 121.0, 0.001)
        assertEquals("誤差過大", point.latitude(), 24.0, 0.001)
    }

    @Test
    fun convertToAngleDisplayText() {
        val angle = "12.0033"
        val text = CoordinateUtil.convertToAngleDisplayText(angle)
        assertEquals("12°00'33\"", text)
    }

    @Test
    fun fromDisplayAngleStringToDegrees() {
        val text = "12°00'33\""
        val angle = CoordinateUtil.fromDisplayAngleStringToDegrees(text)
        assertEquals("誤差過大", 12.009167, angle, 0.000001)
    }
}