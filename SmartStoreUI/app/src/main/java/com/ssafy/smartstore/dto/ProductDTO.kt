package com.ssafy.smartstore.dto

import java.io.Serializable


data class ProductDTO(
    var id: Int,
    var name: String,
    var type: String,
    var price: Int,
    var img: String
) : Serializable





