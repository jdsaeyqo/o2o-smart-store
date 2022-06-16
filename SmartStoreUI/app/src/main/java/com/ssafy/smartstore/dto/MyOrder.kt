package com.ssafy.smartstore.dto

data class MyOrder(
    val oid: Int,
    val img: String,
    val Name: String,
    val totalPrice: Int,
    val date: String,
    val totalQuantity: Int
)
