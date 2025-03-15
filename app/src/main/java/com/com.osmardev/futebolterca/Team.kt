package com.osmardev.futebolterca

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Team(
    val name: String,
    val players: ArrayList<Player> // Agora armazena objetos Player
) : Parcelable
