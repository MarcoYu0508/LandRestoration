package com.mhy.landrestoration.model

data class SelectPointListItem(
    val id: Int,
    var name: String,
    var N: Double,
    var E: Double,
    var isSelected: Boolean = false
)