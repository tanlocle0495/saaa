package com.kabu.myapplication.model

data class CreateResponse(
    val hiCardNo: String,
    val issueDateTime: String,
    val patientCode: String,
    val patientName: String,
    val printTimes: Int,
    val recCreatedDate: String,
    val serialTicket: String,
    val ticketID: Int,
    val ticketNumberText: String,
    val v_TicketStatus: Int,
    val webAccManPtID: Int
)