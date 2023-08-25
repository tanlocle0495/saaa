package com.kabu.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kabu.myapplication.DateTimeUtils.getTomorrow
import com.kabu.myapplication.SingleLiveEvent
import com.kabu.myapplication.api.RemoteDataSource
import com.kabu.myapplication.model.CreateResponse
import com.kabu.myapplication.model.CreateTicketRequest
import com.kabu.myapplication.model.TicketResponse
import com.kabu.myapplication.model.TokenResponse
import com.kabu.myapplication.model.UserInfoResponse
import com.kabu.myapplication.repository.NetworkResult
import com.kabu.myapplication.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository: Repository = Repository(RemoteDataSource())


    private val _loginOb: SingleLiveEvent<NetworkResult<TokenResponse>> =
        SingleLiveEvent<NetworkResult<TokenResponse>>()
    val loginOb: LiveData<NetworkResult<TokenResponse>> = _loginOb

    private val _loginOb2: SingleLiveEvent<NetworkResult<TokenResponse>> =
        SingleLiveEvent<NetworkResult<TokenResponse>>()
    val loginOb2: LiveData<NetworkResult<TokenResponse>> = _loginOb2

    private val _userInfoResponse = SingleLiveEvent<NetworkResult<UserInfoResponse>>()

    val userInfoResponse: LiveData<NetworkResult<UserInfoResponse>> = _userInfoResponse


    private val _userInfoResponse2 = SingleLiveEvent<NetworkResult<UserInfoResponse>>()

    val userInfoResponse2: LiveData<NetworkResult<UserInfoResponse>> = _userInfoResponse2


    private val _createTicket = SingleLiveEvent<NetworkResult<CreateResponse>>()

    val createTicket: LiveData<NetworkResult<CreateResponse>> = _createTicket


    private val _ticket: MutableLiveData<NetworkResult<List<TicketResponse>>> =
        MutableLiveData()

    fun onLogin(
        username: String = "",
        password: String = ""
    ) = viewModelScope.launch {

        repository.login(username, password).collect { values ->
            _loginOb.value = values
        }
    }

    fun onLogin2(
        username: String = "",
        password: String = ""
    ) = viewModelScope.launch {

        repository.login(username, password).collect { values ->
            _loginOb2.value = values
        }
    }

    val ticket: LiveData<NetworkResult<List<TicketResponse>>> = _ticket

    fun fetchUserInfo() = viewModelScope.launch {
        repository.userInfo().collect { values ->
            _userInfoResponse.value = values
        }
    }

    fun fetchUserInfo2() = viewModelScope.launch {
        repository.userInfo().collect { values ->
            _userInfoResponse2.value = values
        }
    }

    fun getTicket(id: String) = viewModelScope.launch {
        repository.getTicketList(id).collect { values ->
            _ticket.value = values
        }
    }

    fun createTicket(user: UserInfoResponse) = viewModelScope.launch {

        repository.onCreateTicket(
            request = CreateTicketRequest(
                aType = 30,
                hiCardNo = "",
                webAccountUserID = user.webUserAccID,
                patientCode = user.pkhid,
                patientName = user.accName,
                ticketGetTime = getTomorrow(),
                webAccManPtID = 896,
            )
        ).collect { values ->
            _createTicket.value = values
        }

    }

}
