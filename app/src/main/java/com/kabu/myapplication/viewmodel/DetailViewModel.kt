package com.kabu.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kabu.myapplication.api.RemoteDataSource
import com.kabu.myapplication.model.TicketResponse
import com.kabu.myapplication.model.TokenResponse
import com.kabu.myapplication.model.UserInfoResponse
import com.kabu.myapplication.repository.NetworkResult
import com.kabu.myapplication.repository.Repository
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    private val repository: Repository = Repository(RemoteDataSource())

    private val _ticket: MutableLiveData<NetworkResult<List<TicketResponse>>> =
        MutableLiveData()
    val ticket: LiveData<NetworkResult<List<TicketResponse>>> = _ticket

    fun getTickets() = viewModelScope.launch {
        repository.getTicketList("106474").collect { values ->
            _ticket.value = values
        }
    }

}