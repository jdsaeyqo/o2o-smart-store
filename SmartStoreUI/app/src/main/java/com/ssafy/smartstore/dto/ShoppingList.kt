package com.ssafy.smartstore.dto

data class ShoppingList(
    val img: String,
    val name: String,
    val price: Int,
    val quantity: Int,
    val totalPrice: Int
)
