package com.example.nbaviewer.ui.state

import com.example.nbaviewer.model.Team

class TeamDetailUiState(
    val id: Long = 0,
    val abbreviation: String = "",
    val city: String = "",
    val conference: String = "",
    val division: String = "",
    val fullName: String = "",
    val name: String = "",
) {
    constructor(team: Team) : this(
        team.id,
        team.abbreviation,
        team.city,
        team.conference,
        team.division,
        team.fullName,
        team.name
    )
}