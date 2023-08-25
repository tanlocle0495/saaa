package com.kabu.myapplication.api

import com.kabu.myapplication.model.TicketResponse
import com.kabu.myapplication.model.TokenResponse
import com.kabu.myapplication.model.UserInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ServiceApi {
    @POST("/connect/token")
    @FormUrlEncoded
    suspend fun onLogin(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("client_id") clientID: String,/*="tdemr_mobile_client_id"*/
        @Field("grant_type") ids: String/* "multi_facility"*/ // Add '[
    ): Response<TokenResponse>

    @POST("/connect/userinfo")
    suspend fun userinfo(): Response<UserInfoResponse>
}
