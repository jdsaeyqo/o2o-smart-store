package com.ssafy.smartstore.dto


data class UserDTO(
    var id: String,
    var name: String,
    var pass: String,
    var stamps: Int = 0
)


