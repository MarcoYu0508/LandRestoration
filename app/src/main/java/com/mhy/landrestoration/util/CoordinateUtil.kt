package com.mhy.landrestoration.util

import com.mapbox.geojson.Point
import kotlin.math.*

object CoordinateUtil {
    fun twd97ToLatLng(_x: Double, _y: Double): Point {
        var x = _x
        var y = _y
        val a = 6378137.0
        val b = 6356752.314245
        val lng0 = 121 * PI / 180
        val k0 = 0.9999
        val dx = 250000.0
        val dy = 0.0
        val e = (1 - b.pow(2.0) / a.pow(2.0)).pow(0.5)

        x -= dx
        y -= dy

        val m = y / k0

        val mu =
            m / (a * (1.0 - e.pow(2.0) / 4.0 - 3 * e.pow(4.0) / 64.0 - 5 * e.pow(6.0) / 256.0))
        val e1 = (1.0 - (1.0 - e.pow(2.0)).pow(0.5)) / (1.0 + (1.0 - e.pow(2.0)).pow(0.5))

        val j1 = 3 * e1 / 2 - 27 * e1.pow(3.0) / 32.0
        val j2 = 21 * e1.pow(2.0) / 16 - 55 * e1.pow(4.0) / 32.0
        val j3 = 151 * e1.pow(3.0) / 96.0
        val j4 = 1097 * e1.pow(4.0) / 512.0

        val fp =
            mu + j1 * sin(2 * mu) + j2 * sin(4 * mu) + j3 * sin(6 * mu) + j4 * sin(8 * mu)

        val e2 = (e * a / b).pow(2.0)
        val c1 = (e2 * cos(fp)).pow(2.0)
        val t1 = tan(fp).pow(2.0)
        val r1 = a * (1 - e.pow(2.0)) / (1 - e.pow(2.0) * sin(fp).pow(2.0)).pow(3.0 / 2.0)
        val n1 = a / (1 - e.pow(2.0) * sin(fp).pow(2.0)).pow(0.5)

        val d = x / (n1 * k0)

        val q1 = n1 * tan(fp) / r1
        val q2 = d.pow(2.0) / 2.0
        val q3 =
            (5 + 3 * t1 + 10 * c1 - 4 * c1.pow(2.0) - 9 * e2) * d.pow(4.0) / 24.0
        val q4 =
            (61 + 90 * t1 + 298 * c1 + 45 * t1.pow(2.0) - 3 * c1.pow(2.0) - 252 * e2) * d.pow(
                6.0
            ) / 720.0
        var lat = fp - q1 * (q2 - q3 + q4)

        val q6 = (1 + 2 * t1 + c1) * d.pow(3.0) / 6
        val q7 =
            (5 - 2 * c1 + 28 * t1 - 3 * c1.pow(2.0) + 8 * e2 + 24 * t1.pow(2.0)) * d.pow(5.0) / 120.0
        var lng = lng0 + (d - q6 + q7) / cos(fp)

        lat = lat * 180 / PI
        lng = lng * 180 / PI

        return Point.fromLngLat(lng, lat)
    }

    fun convertToAngleDisplayText(angelStr: String): String {
        val angleMinSec = angelStr.split("\\.".toRegex())
        var angle = ""
        var min = ""
        var sec = ""
        if (angleMinSec.size == 1) {
            angle = angelStr
        } else if (angleMinSec.size == 2) {
            angle = angleMinSec[0]
            val minSec = angleMinSec[1]
            if (minSec.length <= 2) {
                min = minSec
            } else {
                min = minSec.substring(0, 2)
                sec = minSec.substring(2)
            }
        }
        return (if (angle != "") angle else "0") + "°" + (if (min != "") "$min'" else "") + if (sec != "") sec + "\"" else ""
    }

     fun fromDisplayAngleStringToDegrees(displayStr: String): Double {
        var angle = 0.0
        val degree = displayStr.splitIgnoreEmpty("°")
        if (degree.size == 1) {
            if (degree[0] != "") angle += degree[0].toDouble()
        } else {
            if (degree[0] != "") angle += degree[0].toDouble()
            val min = degree[1].splitIgnoreEmpty("'")
            if (min.size == 1) {
                if (min[0] != "") angle += min[0].toDouble() / 60
            } else {
                if (min[0] != "") angle += min[0].toDouble() / 60
                val sec = min[1].splitIgnoreEmpty("\"")
                if (sec.size == 1) {
                    angle += sec[0].toDouble() / 3600
                }
            }
        }
        return angle
    }
}