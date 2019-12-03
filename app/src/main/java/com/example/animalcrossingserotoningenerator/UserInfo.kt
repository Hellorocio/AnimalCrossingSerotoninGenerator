package com.example.animalcrossingserotoningenerator

// Firebase demands an empty constructor, so all fields must be optional
data class UserInfo(
    var aboutMe: String? = null,
    var email: String? = null,
    var imageName: String? = null,
    var name: String? = null,
    var personalityType: Int? = null
)