package com.kabu.myapplication.model

data class CreateTicketRequest(
    val aType: Int,
    val hiCardNo: String,
    val patientCode: String,
    val patientName: String,
    val ticketGetTime: String,
    val webAccManPtID: Int,
    val webAccountUserID: String
)

//aType
//:
//30
//hiCardNo
//:
//""
//patientCode
//:
//""
//patientName
//:
//"Lê Tấn Lộc"
//ticketGetTime
//:
//"2023-08-03T00:18:00"
//webAccManPtID
//:
//896
//webAccountUserID
//:
//"106474"