package com.snn.repostock

data class Worker(
    val uid: String,
    val name: String,
    val phoneNumber: String,
    val mail: String,
    val salary: Long,
    val promotion: Long,
    val gift: Long,
    val hours: List<Int>,
    val sales: List<Int>,
    val customers: List<Int>,
    val isAdmin: Boolean
)