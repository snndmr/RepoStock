package com.snn.repostock

data class Worker(
    var uid: String,
    val name: String,
    val phoneNumber: String,
    val mail: String,
    val salary: Double,
    val promotion: Double,
    val gift: Double,
    val hours: List<Int>,
    val sales: List<Int>,
    val customers: List<Int>,
    val isAdmin: Boolean
)