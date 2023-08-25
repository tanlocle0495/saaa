package com.kabu.myapplication.api

import com.kabu.myapplication.model.CreateTicketRequest

class RemoteDataSource {
    suspend fun onLogin(username: String, password: String) =
        RetrofitSingletone.serviceApi().onLogin(
            username = username,
            password = password,
            clientID = "tdemr_mobile_client_id",
            ids = "multi_facility"
        )

    suspend fun userInfo() = RetrofitSingletone.serviceApi().userinfo()
    suspend fun getListTicket(id: String) = RetrofitSingletone.serviceApiPRXW().getListTicket(id)
    suspend fun createTicketRequest(request: CreateTicketRequest) =
        RetrofitSingletone.serviceApiPRXW().createTicket(request)
}

