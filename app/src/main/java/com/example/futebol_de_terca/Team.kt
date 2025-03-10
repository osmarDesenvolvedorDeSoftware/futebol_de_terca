package com.example.futebol_de_terca

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Team(
    val name: String,
    val players: ArrayList<String>
) : Parcelable