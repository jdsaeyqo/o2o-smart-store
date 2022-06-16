package com.ssafy.smartstore.dto


data class OrderDetailDTO(
    var id: Int,
    var orderId: Int,
    var productId: Int,
    var quantity: Int = 1
)
