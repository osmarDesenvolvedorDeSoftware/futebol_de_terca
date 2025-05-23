package com.osmardev.futebolterca

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Player(
    val name: String,
    val rating: Int
) : Parcelable


@Parcelize
data class Match(
    val team1: Team,
    val team2: Team,
    var score1: Int? = null,
    var score2: Int? = null
) : Parcelable {
    fun winner(): Team? {
        if (score1 == null || score2 == null) return null
        return when {
            score1!! > score2!! -> team1
            score2!! > score1!! -> team2
            else -> listOf(team1, team2).random()
        }
    }
}

fun generateKnockoutMatches(teams: List<Team>): List<Match> {
    if (teams.size < 2) return emptyList()
    val shuffledTeams = teams.shuffled().toMutableList()
    val matches = mutableListOf<Match>()
    while (shuffledTeams.size >= 2) {
        matches.add(Match(shuffledTeams.removeAt(0), shuffledTeams.removeAt(0)))
    }
    return matches
}

fun advanceToNextRound(matches: List<Match>): List<Match>? {
    val winners = matches.mapNotNull { it.winner() }
    return if (winners.size >= 2) generateKnockoutMatches(winners) else null
}