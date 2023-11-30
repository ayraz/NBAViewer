package com.example.nbaviewer.ui.state

import com.example.nbaviewer.model.Player

class PlayerDetailUiState(
    val id: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    val position: String = "",
    val heightFeet: String = "",
    val heightInches: String = "",
    val weightPounds: String = "",
    val team: String = "",
    val teamId: Long = 0
) {
    constructor(player: Player) : this(
        player.id,
        player.firstName,
        player.lastName,
        player.position,
        player.heightFeet?.toString() ?: "",
        player.heightInches?.toString() ?: "",
        player.weightPounds?.toString() ?: "",
        player.team.name,
        player.team.id
    )
}
