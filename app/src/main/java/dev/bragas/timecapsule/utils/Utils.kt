package dev.bragas.timecapsule.utils

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object Utils {
    fun utcToLocalDate(utc: String): String? {
        val utcDateTime = ZonedDateTime.parse(utc)
        val localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        return localDateTime.format(formatter)
    }
}