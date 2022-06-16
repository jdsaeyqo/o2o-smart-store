package com.ssafy.smartstore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.smartstore.IntentApplication
import com.ssafy.smartstore.dto.MyOrder
import com.ssafy.smartstore.service.OrderService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecentOrderViewModel : ViewModel() {

    private val _recentOrderList = MutableLiveData<MutableList<MyOrder>>()
    val recentOrderList: LiveData<MutableList<MyOrder>>
        get() = _recentOrderList

    fun getRecentOrderList(userId: String){

        viewModelScope.launch(Dispatchers.IO){
            val list = mutableListOf<MyOrder>()

            val api = IntentApplication.retrofit.create(OrderService::class.java)
            val res = api.getLastMonthOrder(userId)

            if(res.isSuccessful && res.body() != null){
                val result = res.body()!!
                if (result.isNotEmpty()) {
                    var preOid = (result[0]["o_id"] as Double).toInt()
                    var date = result[0]["order_time"] as String
                    var totalPrice = 0
                    var firstProductName = result[0]["name"] as String
                    var productImg = result[0]["img"] as String
                    var totalQuantity = 0

                    for (i in result.indices) {
                        if ((result[i]["o_id"] as Double).toInt() == preOid) {
                            totalPrice += (result[i]["price"] as Double).toInt() * (result[i]["quantity"] as Double).toInt()
                            totalQuantity += (result[i]["quantity"] as Double).toInt()

                            if (i == result.size - 1) {
                                list.add(
                                    MyOrder(
                                        preOid, productImg, firstProductName, totalPrice,
                                        date, totalQuantity
                                    )
                                )
                            }
                        } else {
                            list.add(
                                MyOrder(
                                    preOid,
                                    productImg,
                                    firstProductName,
                                    totalPrice,
                                    date,
                                    totalQuantity
                                )
                            )
                            preOid = (result[i]["o_id"] as Double).toInt()
                            date = result[i]["order_time"] as String
                            totalPrice =
                                (result[i]["price"] as Double).toInt() * (result[i]["quantity"] as Double).toInt()
                            firstProductName = result[i]["name"] as String
                            productImg = result[i]["img"] as String
                            totalQuantity = (result[i]["quantity"] as Double).toInt()
                        }
                    }
                }
            }

            _recentOrderList.postValue(list)

        }

    }
}