package com.ssafy.smartstore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.smartstore.IntentApplication
import com.ssafy.smartstore.dto.UserDTO
import com.ssafy.smartstore.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val _isUsed = MutableLiveData<Boolean>()
    val isUsed: LiveData<Boolean>
        get() = _isUsed

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean>
        get() = _loginSuccess

    private val _user = MutableLiveData<UserDTO>()
    val user: LiveData<UserDTO>
        get() = _user

    private val _userInfo = MutableLiveData<Map<String, Any>>()
    val userInfo: LiveData<Map<String, Any>>
        get() = _userInfo

    fun checkIsUsed(id: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val api = IntentApplication.retrofit.create(UserService::class.java)
            val res = api.isUsed(id)
            if (res.isSuccessful && res.body() != null) {
                _isUsed.postValue(res.body())
            }
        }

    }

    fun insertUser(user: UserDTO) {

        viewModelScope.launch(Dispatchers.IO) {
            val api = IntentApplication.retrofit.create(UserService::class.java)
            api.insert(user).body()!!

        }

    }

    fun loginUser(user: UserDTO) {

        viewModelScope.launch(Dispatchers.IO) {
            val api = IntentApplication.retrofit.create(UserService::class.java)
            val result = api.login(user)

            if (result.isSuccessful && result.body() != null) {
                Log.d("loginUser", "${result.body()}")
                val userDto = result.body()!!
                if (userDto.id == "") {
                    _loginSuccess.postValue(false)
                } else {
                    _user.postValue(result.body())
                    _loginSuccess.postValue(true)
                }

            }

        }

    }

    fun getInfo(userId : String){
        viewModelScope.launch(Dispatchers.IO){
            val api = IntentApplication.retrofit.create(UserService::class.java)
            val result = api.getInfo(userId)

            if(result.isSuccessful && result.body() != null){
                val info = result.body()!!
                _userInfo.postValue(info)
            }
        }

    }

}