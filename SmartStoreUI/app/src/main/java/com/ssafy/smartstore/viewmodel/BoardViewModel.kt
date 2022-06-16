package com.ssafy.smartstore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.smartstore.IntentApplication
import com.ssafy.smartstore.dto.Board
import com.ssafy.smartstore.service.BoardService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BoardViewModel : ViewModel() {

    private val _boardList = MutableLiveData<MutableList<Board>>()
    val boardList: LiveData<MutableList<Board>>
        get() = _boardList

    private val _insertResult = MutableLiveData<Boolean>()
    val insertResult : LiveData<Boolean>
    get() = _insertResult

    private val _deleteResult = MutableLiveData<Boolean>()
    val deleteResult : LiveData<Boolean>
        get() = _deleteResult

    fun selectAllBoards(){
        viewModelScope.launch(Dispatchers.IO){
            val api = IntentApplication.retrofit.create(BoardService::class.java)
            val result = api.selectBoards()

            if(result.isSuccessful && result.body() != null){
                _boardList.postValue(result.body()!!)
            }
        }
    }

    fun insertBoard(board : Board){
        viewModelScope.launch(Dispatchers.IO){
            val api = IntentApplication.retrofit.create(BoardService::class.java)
            val result = api.insertBoard(board)

            if(result.isSuccessful && result.body() != null){
                _insertResult.postValue(result.body()!!)
            }
        }

    }

    fun deleteBoard(id : Int){
        viewModelScope.launch(Dispatchers.IO){
            val api = IntentApplication.retrofit.create(BoardService::class.java)
            val result = api.delete(id)

            if(result.isSuccessful && result.body() != null){
                _deleteResult.postValue(result.body()!!)
            }
        }

    }

}