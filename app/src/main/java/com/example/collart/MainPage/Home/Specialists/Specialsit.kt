package com.example.collart.MainPage.Home.Specialists

import com.example.collart.MainPage.Home.Projects.Experience

data class Specialist(val id: String, val name: String, val backGroundImage: String, val profession: List<String>, val avatarImage: String, val experience: Experience, val programs: List<String>)