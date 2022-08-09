package com.mhy.landrestoration.model

data class SelectedPointItem(
    val title: String,
    var name: String?,
    var N: Double?,
    var E: Double?,
    val isDeletable: Boolean = false
)