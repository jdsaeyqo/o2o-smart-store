package com.ssafy.smartstore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.smartstore.IntentApplication
import com.ssafy.smartstore.dto.OrderDTO
import com.ssafy.smartstore.dto.OrderDetailDTO
import com.ssafy.smartstore.dto.StampDTO
import com.ssafy.smartstore.service.OrderService
import com.ssafy.smartstore.util.FCMUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OrderViewModel : ViewModel() {

    private val _orderDetail = MutableLiveData<MutableList<Map<String, Any>>>()
    val orderDetail: LiveData<MutableList<Map<String, Any>>>
        get() = _orderDetail

    private val _orderResult = MutableLiveData<Int>()
    val orderResult: LiveData<Int>
        get() = _orderResult

    private val _orderLastOrderId = MutableLiveData<Int>()
    val orderLastOrderId: LiveData<Int>
        get() = _orderLastOrderId

    fun getOrderDetail(oid: Int) {

        viewModelScope.launch(Dispatchers.IO) {
            val api = IntentApplication.retrofit.create(OrderService::class.java)
            val result = api.getOrderDetail(oid)

            if (result.isSuccessful && result.body() != null) {
                _orderDetail.postValue(result.body()!!)
            }
        }

    }

    fun makeOrder(
        oid: Int,
        userId: String,
        tableNum : Int,
        detailList: MutableList<OrderDetailDTO>
    ) {

        viewModelScope.launch(Dispatchers.IO){

            val now = System.currentTimeMillis()
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
            formatter.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val date = formatter.format(now)

            var stampsQuantity =0
            detailList.forEach {
                stampsQuantity += it.quantity
            }

            val order = OrderDTO(
                oid,
                userId,
                "order_table $tableNum",
                detailList,
                date,
                "N",
                StampDTO(0,userId,oid,stampsQuantity)
            )
            FCMUtil.setMessageToSharedPreference("주문이 완료되었습니다")


            val api = IntentApplication.retrofit.create(OrderService::class.java)
            val result = api.makeOrder(order)

            if(result.isSuccessful && result.body() != null){
                _orderResult.postValue(result.body()!!)
            }

        }

    }

    fun getLastOrderId(){
        viewModelScope.launch(Dispatchers.IO){
            val api = IntentApplication.retrofit.create(OrderService::class.java)
            val result = api.getLastOrderId()

            if(result.isSuccessful && result.body() != null){
                _orderLastOrderId.postValue(result.body()!!)
            }
        }

    }

}