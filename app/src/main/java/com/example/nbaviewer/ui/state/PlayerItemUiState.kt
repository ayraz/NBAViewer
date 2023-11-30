package com.example.nbaviewer.ui.state

import com.example.nbaviewer.model.Player

data class PlayerItemUiState(
    val firstName: String,
    val lastName: String,
    val position: String,
    val team: String,
    val id: Long
) {
    constructor(player: Player) : this(
        player.firstName,
        player.lastName,
        player.position,
        player.team.name,
        player.id
    )
}