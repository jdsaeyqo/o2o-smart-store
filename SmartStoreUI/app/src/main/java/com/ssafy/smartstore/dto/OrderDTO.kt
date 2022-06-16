package com.ssafy.smartstore.dto


data class OrderDTO (
    var id :Int,
    var userId:String,
    var orderTable:String,
    var details : List<OrderDetailDTO>,
    val orderTime: String,
    val completed: String,
    val stamp : StampDTO
)
