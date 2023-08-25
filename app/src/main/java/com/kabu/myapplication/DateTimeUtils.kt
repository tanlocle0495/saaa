package com.kabu.myapplication

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date


object DateTimeUtils {


    fun dayDisplay(dateString: String): String {
        val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
        val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return LocalDateTime.parse(dateString, inputFormatter).format(outputFormatter);
    }

    fun getTomorrow(): String {
        val today = LocalDateTime.now()
        val tomorrow = today.plusDays(1)

        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val tomorrowISO = tomorrow.format(formatter)
        return tomorrowISO;

    }
}