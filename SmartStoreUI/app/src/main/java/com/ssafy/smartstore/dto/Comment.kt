package com.ssafy.smartstore.dto

data class Comment(
    val id: Int,
    val userId: String,
    val productId: Int,
    val rating: Double,
    var comment: String
)
