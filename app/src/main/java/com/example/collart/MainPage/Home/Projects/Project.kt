package com.example.collart.MainPage.Home.Projects

import com.example.collart.NetworkSystem.Tool

enum class Experience(val stringValue: String){
    NO_EXPERIENCE("Нет опыта"),
    FROM_1_TO_3_YEARS("От 1 года до 3 лет"),
    FROM_3_TO_5_YEARS("От 3 до 5 лет"),
    MORE_THAN_5_YEARS("Более 5 лет");

    companion object {

        fun fromString(value: String): Experience {
            when (value) {
                "no_experience" -> {
                    return NO_EXPERIENCE
                }
                "1-3_years" -> {
                    return FROM_1_TO_3_YEARS
                }
                "3-5_years" -> {
                    return FROM_3_TO_5_YEARS
                }
                "more_than_5_years" -> {
                    return MORE_THAN_5_YEARS
                }
            }
            return NO_EXPERIENCE
        }

        fun fromNameToString(value: String): String{
            when (value) {
                "Нет опыта" -> {
                    return "no_experience"
                }
                "От 1 года до 3 лет" -> {
                    return "1-3_years"
                }
                "От 3 до 5 лет" -> {
                    return "3-5_years"
                }
                "Более 5 лет" -> {
                    return "more_than_5_years"
                }
            }
            return "no_experience"
        }
    }
}

fun Experience.toStringValue(): String {
    return when (this) {
        Experience.NO_EXPERIENCE -> {
            "no_experience"
        }
        Experience.FROM_1_TO_3_YEARS -> {
            "1-3_years"
        }
        Experience.FROM_3_TO_5_YEARS -> {
            "3-5_years"
        }
        Experience.MORE_THAN_5_YEARS -> {
            "more_than_5_years"
        }
    }
}

data class Project(val id: String, val name: String, val image: String, val profession: String, val description: String, val experience: Experience, val programs: List<String>, val authorImage: String, val authorName: String, val authorId: String) {
}