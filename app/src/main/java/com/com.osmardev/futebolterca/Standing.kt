package com.osmardev.futebolterca



import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Standing(
    val team: Team,
    var points: Int,
    var wins: Int,
    var draws: Int,
    var losses: Int
) : Parcelable