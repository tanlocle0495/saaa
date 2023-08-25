package com.kabu.myapplication.api

import com.kabu.myapplication.model.CreateResponse
import com.kabu.myapplication.model.CreateTicketRequest
import com.kabu.myapplication.model.TicketResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ServicePRXWApi {

    @POST("/QMS/App_GetListTicketByWebAccountUserID")
    suspend fun getListTicket(@Body value: String): Response<List<TicketResponse>>

    @POST("/QMS/App_CreateNewTicket")
    suspend fun createTicket(@Body request: CreateTicketRequest): Response<CreateResponse>
}
