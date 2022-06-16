package com.ssafy.smartstore.dto


data class StampDTO(
    var id: Int = 0,
    var user_id: String,
    var order_id: Int,
    var quantity: Int = 0
)



