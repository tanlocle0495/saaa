package com.kabu.myapplication.model

data class createRequest(
    val aType: Int,
    val hiCardNo: String,
    val patientCode: String,
    val patientName: String,
    val ticketGetTime: String,
    val webAccManPtID: Int,
    val webAccountUserID: String
)