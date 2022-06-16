package com.ssafy.smartstore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.smartstore.IntentApplication
import com.ssafy.smartstore.dto.ProductDTO
import com.ssafy.smartstore.service.ProductService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private val _productList = MutableLiveData<MutableList<ProductDTO>>()
    val productList: LiveData<MutableList<ProductDTO>>
        get() = _productList

    fun getProductList(){
        viewModelScope.launch(Dispatchers.IO){

            val list = mutableListOf<ProductDTO>()

            val api = IntentApplication.retrofit.create(ProductService::class.java)
            val res = api.getProductList()

            if(res.isSuccessful && res.body() != null){
                res.body()!!.forEach { product ->
                    list.add(product)
                }
                _productList.postValue(list)
            }


        }
    }
}