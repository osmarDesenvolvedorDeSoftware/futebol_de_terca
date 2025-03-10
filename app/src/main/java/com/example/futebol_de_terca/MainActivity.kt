package com.example.futebol_de_terca

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnGenerateTeams: Button
    private lateinit var btnStartChampionship: Button
    private lateinit var etPlayers: EditText
    private lateinit var etTeamCount: EditText
    private lateinit var etPlayersPerTeam: EditText
    private lateinit var spTournamentType: Spinner
    private lateinit var tvGeneratedTeams: TextView

    private var generatedTeams: List<Team> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGenerateTeams = findViewById(R.id.btnGenerateTeams)
        btnStartChampionship = findViewById(R.id.btnStartChampionship)
        etPlayers = findViewById(R.id.etPlayers)
        etTeamCount = findViewById(R.id.etTeamCount)
        etPlayersPerTeam = findViewById(R.id.etPlayersPerTeam)
        spTournamentType = findViewById(R.id.spTournamentType)
        tvGeneratedTeams = findViewById(R.id.tvGeneratedTeams)

        btnGenerateTeams.setOnClickListener { generateTeams() }
        btnStartChampionship.setOnClickListener { startTournament() }
    }

    private fun generateTeams() {
        val playersInput = etPlayers.text.toString().trim()
        val teamCount = etTeamCount.text.toString().toIntOrNull() ?: 0
        val playersPerTeam = etPlayersPerTeam.text.toString().toIntOrNull() ?: 0
        val tournamentType = spTournamentType.selectedItem.toString()

        if (playersInput.isEmpty() || teamCount == 0 || playersPerTeam == 0) {
            showToast("Preencha todos os campos!")
            return
        }

        val players = playersInput.split(",").map { it.trim() }.shuffled()
        val totalPlayersNeeded = teamCount * playersPerTeam

        if (players.size < totalPlayersNeeded) {
            showToast("Faltam jogadores! Necessários: $totalPlayersNeeded")
            return
        }

        if (tournamentType == "Mata-Mata" && !teamCount.isPowerOfTwo()) {
            showToast("Para mata-mata, o número de times deve ser 2, 4, 8...")
            return
        }

        generatedTeams = List(teamCount) { index ->
            val start = index * playersPerTeam
            val end = start + playersPerTeam
            Team("Time ${index + 1}", ArrayList(players.subList(start, end)))
        }

        tvGeneratedTeams.text = generatedTeams.joinToString("\n\n") { team ->
            "${team.name}: ${team.players.joinToString(", ")}"
        }

        btnStartChampionship.isEnabled = true
        showToast("Times gerados!")
    }

    private fun startTournament() {
        val intent = when (spTournamentType.selectedItem.toString()) {
            "Mata-Mata" -> Intent(this, KnockoutActivity::class.java)
            "Pontos Corridos" -> Intent(this, LeagueActivity::class.java)
            else -> {
                showToast("Modo não implementado")
                return
            }
        }
        intent.putExtra("teams", ArrayList(generatedTeams))
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun Int.isPowerOfTwo(): Boolean = this > 0 && (this and (this - 1)) == 0
}