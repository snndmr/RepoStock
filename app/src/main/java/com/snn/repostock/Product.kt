package com.snn.repostock

class Product(
    val name: String,
    val category: String,
    val price: Double,
    val stock: Long,
    val stocksChange: List<Int>,
    val barcode: String
)
