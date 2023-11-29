package com.example.nbaviewer.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Team(
    val id: Long = 0,
    val abbreviation: String = "",
    val city: String = "",
    val conference: String = "",
    val division: String = "",
    @SerialName("full_name")
    val fullName: String = "",
    val name: String = "",
)