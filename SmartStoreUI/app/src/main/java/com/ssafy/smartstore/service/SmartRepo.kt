package com.ssafy.smartstore.service

import com.ssafy.smartstore.dto.OrderDTO
import com.ssafy.smartstore.dto.ProductDTO
import com.ssafy.smartstore.dto.UserDTO
import com.ssafy.smartstore.dto.Board
import com.ssafy.smartstore.dto.Comment
import retrofit2.Response
import retrofit2.http.*

interface UserService{

    @GET("rest/user/isUsed")
    suspend fun isUsed(@Query("id") id : String) : Response<Boolean>

    @POST("rest/user/info")
    suspend fun getInfo(@Query("id") id : String) : Response<Map<String,Any>>

    @POST("rest/user/login")
    suspend fun login(@Body user : UserDTO) : Response<UserDTO>

    @POST("rest/user")
    suspend fun insert(@Body user : UserDTO) : Response<Boolean>

}

interface OrderService{

    @GET("rest/order/byUser")
    suspend fun getLastMonthOrder(@Query("id") id : String) : Response<List<Map<String,Any>>>

    @POST("rest/order")
    suspend fun makeOrder(@Body order : OrderDTO) : Response<Int>

    @GET("rest/order/all")
    suspend fun getLastOrderId():Response<Int>

    @GET("rest/order/{orderId}")
    suspend fun getOrderDetail(@Path("orderId") oid : Int) : Response<MutableList<Map<String,Any>>>

}

interface ProductService{
    @GET("rest/product")
    suspend fun getProductList() : Response<MutableList<ProductDTO>>
}

interface CommentService{

    @GET("rest/comment/{productId}")
    suspend fun selectByProduct(@Path("productId") pid : Int) : Response<MutableList<Comment>>

    @DELETE("rest/comment/{id}")
    suspend fun delete(@Path("id") id : Int) : Response<Boolean>

    @PUT("rest/comment")
    suspend fun update(@Body comment : Comment) : Response<Boolean>

    @POST("rest/comment")
    suspend fun insert(@Body comment : Comment) : Response<Boolean>

}

interface BoardService{
    @GET("rest/board/all")
    suspend fun selectBoards() : Response<MutableList<Board>>

    @POST("rest/board")
    suspend fun insertBoard(@Body board : Board) : Response<Boolean>

    @DELETE("rest/board/{id}")
    suspend fun delete(@Path("id") id : Int) : Response<Boolean>
}