package com.osmardev.futebolterca


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.osmardev.futebolterca.LeagueActivity

class LeagueActivity : AppCompatActivity() {

    private lateinit var recyclerViewMatches: RecyclerView
    private lateinit var recyclerViewStandings: RecyclerView
    private lateinit var matchAdapter: MatchAdapter
    private lateinit var standingsAdapter: StandingsAdapter
    private lateinit var teams: List<Team>
    private lateinit var matches: List<Match>
    private lateinit var standings: MutableList<Standing>

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_league)

        recyclerViewMatches = findViewById(R.id.recyclerViewMatches)
        recyclerViewStandings = findViewById(R.id.recyclerViewStandings)


        teams = (intent.getSerializableExtra("teams") as? ArrayList<Team>)?.toList() ?: emptyList()

        if (teams.isEmpty()) {
            Toast.makeText(this, "Nenhum time recebido!", Toast.LENGTH_SHORT).show()
            finish()
        }

        matches = generateLeagueMatches(teams)
        standings = calculateStandings(matches)

        setupRecyclerViews()
    }

    private fun generateLeagueMatches(teams: List<Team>): List<Match> {
        val matches = mutableListOf<Match>()
        for (i in teams.indices) {
            for (j in i + 1 until teams.size) {
                matches.add(Match(teams[i], teams[j]))
            }
        }
        return matches
    }

    private fun calculateStandings(matches: List<Match>): MutableList<Standing> {
        val standings = mutableListOf<Standing>()
        for (team in teams) {
            standings.add(Standing(team, 0, 0, 0, 0))
        }

        for (match in matches) {
            val standingTeam1 = standings.find { it.team == match.team1 }!!
            val standingTeam2 = standings.find { it.team == match.team2 }!!

            when {
                match.score1 == null || match.score2 == null -> continue
                match.score1!! > match.score2!! -> {
                    standingTeam1.points += 3
                    standingTeam1.wins += 1
                    standingTeam2.losses += 1
                }
                match.score2!! > match.score1!! -> {
                    standingTeam2.points += 3
                    standingTeam2.wins += 1
                    standingTeam1.losses += 1
                }
                else -> {
                    standingTeam1.points += 1
                    standingTeam2.points += 1
                    standingTeam1.draws += 1
                    standingTeam2.draws += 1
                }
            }
        }

        standings.sortByDescending { it.points }
        return standings
    }

    private fun setupRecyclerViews() {
        recyclerViewMatches.layoutManager = LinearLayoutManager(this)
        matchAdapter = MatchAdapter(matches) {
            standings = calculateStandings(matches)
            standingsAdapter.updateStandings(standings)
        }
        recyclerViewMatches.adapter = matchAdapter

        recyclerViewStandings.layoutManager = LinearLayoutManager(this)
        standingsAdapter = StandingsAdapter(standings)
        recyclerViewStandings.adapter = standingsAdapter
    }
}