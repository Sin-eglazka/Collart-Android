package com.example.collart.Tools.TimeConverter

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeConverter {
    fun GetDateTime(s: String): String {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = dateFormat.parse(s)
            val timestamp = date.time
            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            val netDate = Date(timestamp)
            sdf.format(netDate)
        } catch (e: Exception) {
            s
        }
    }

    fun GetTimeOfChat(s: String) : String{
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = dateFormat.parse(s)
            val parsedCalendar = Calendar.getInstance().apply {
                time = date
            }
            val currentCalendar = Calendar.getInstance()

            if (parsedCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                parsedCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
                parsedCalendar.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH)){
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(parsedCalendar.time)
            }
            else{
                SimpleDateFormat("MMM dd", Locale.getDefault()).format(parsedCalendar.time)
            }

        } catch (e: Exception) {
            s
        }
    }

    fun GetTimeOfDate(s: String) : String{
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = dateFormat.parse(s)
            val parsedCalendar = Calendar.getInstance().apply {
                time = date
            }
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(parsedCalendar.time)


        } catch (e: Exception) {
            s
        }
    }

    fun GetDayOfDate(s: String) : String{
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = dateFormat.parse(s)
            val parsedCalendar = Calendar.getInstance().apply {
                time = date
            }
            SimpleDateFormat("MMMM dd", Locale.getDefault()).format(parsedCalendar.time)

        } catch (e: Exception) {
            s
        }
    }
}