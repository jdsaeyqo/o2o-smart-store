package com.ssafy.smartstore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.smartstore.IntentApplication
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.service.CommentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentViewModel : ViewModel() {

    private val _commentList = MutableLiveData<MutableList<Comment>>()
    val commentList: LiveData<MutableList<Comment>>
        get() = _commentList

    private val _insertResult = MutableLiveData<Boolean>()
    val insertResult: LiveData<Boolean>
        get() = _insertResult

    private val _updateResult = MutableLiveData<Boolean>()
    val updateResult: LiveData<Boolean>
        get() = _updateResult

    private val _deleteResult = MutableLiveData<Boolean>()
    val deleteResult: LiveData<Boolean>
        get() = _deleteResult

    fun getCommentList(productId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val api = IntentApplication.retrofit.create(CommentService::class.java)
            val result = api.selectByProduct(productId)

            if (result.isSuccessful && result.body() != null) {
                _commentList.postValue(result.body()!!)
            }
        }
    }

    fun insertComment(comment: Comment) {
        viewModelScope.launch(Dispatchers.IO) {
            val api = IntentApplication.retrofit.create(CommentService::class.java)
            val result = api.insert(comment)

            if (result.isSuccessful && result.body() != null) {
                _insertResult.postValue(true)
            }
        }
    }

    fun updateComment(comment: Comment) {
        viewModelScope.launch(Dispatchers.IO) {
            val api = IntentApplication.retrofit.create(CommentService::class.java)
            val result = api.update(comment)

            if (result.isSuccessful && result.body() != null) {
                _updateResult.postValue(true)
            }
        }
    }

    fun deleteComment(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val api = IntentApplication.retrofit.create(CommentService::class.java)
            val result = api.delete(id)

            if (result.isSuccessful && result.body() != null) {
                _deleteResult.postValue(true)
            }
        }
    }


}