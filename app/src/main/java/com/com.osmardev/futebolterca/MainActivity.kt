package com.osmardev.futebolterca

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class MainActivity : AppCompatActivity() {

    private lateinit var btnGenerateTeams: Button
    private lateinit var btnStartChampionship: Button
    private lateinit var etPlayers: EditText
    private lateinit var etTeamCount: EditText
    private lateinit var etPlayersPerTeam: EditText
    private lateinit var spTournamentType: MaterialAutoCompleteTextView
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


        val tournamentTypes = resources.getStringArray(R.array.tournament_types)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tournamentTypes)
        spTournamentType.setAdapter(adapter)


        spTournamentType.setOnClickListener {
            spTournamentType.showDropDown()
        }

        btnGenerateTeams.setOnClickListener { generateTeams() }
        btnStartChampionship.setOnClickListener { startTournament() }
    }

    private fun generateTeams() {
        val playersInput = etPlayers.text.toString().trim()
        val players = parsePlayers(playersInput)
        val teamCount = etTeamCount.text.toString().toIntOrNull() ?: 0

        if (playersInput.isEmpty() || teamCount == 0) {
            showToast("Preencha os campos corretamente!")
            return
        }

        if (players.size < teamCount * 2) {
            showToast("Faltam jogadores para formar os times!")
            return
        }

        generatedTeams = generateBalancedTeams(players, teamCount)
        tvGeneratedTeams.text = generatedTeams.joinToString("\n\n") { team ->
            "${team.name}: ${team.players.joinToString(", ") { "${it.name} (${it.rating}★)" }}"
        }
        btnStartChampionship.isEnabled = true
        showToast("Times equilibrados gerados!")
    }

    private fun startTournament() {
        if (generatedTeams.isEmpty()) {
            showToast("Erro: Nenhum time gerado!")
            return
        }

        val tournamentType = spTournamentType.text.toString()
        val intent = when (tournamentType) {
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

    private fun parsePlayers(input: String): List<Player> {
        return input.split(",").map { rawPlayer ->
            val trimmed = rawPlayer.trim()
            val name = trimmed.replace("*", "")
            val rating = trimmed.count { it == '*' }.coerceIn(1, 5)
            Player(name, rating)
        }
    }

    private fun generateBalancedTeams(players: List<Player>, teamCount: Int): List<Team> {
        val sortedPlayers = players.sortedByDescending { it.rating }
        val teams = MutableList(teamCount) { mutableListOf<Player>() }
        sortedPlayers.forEachIndexed { index, player ->
            teams[index % teamCount].add(player)
        }
        return teams.map { Team(generateRandomTeamName(), ArrayList(it)) }
    }

    private fun generateRandomTeamName(): String {
        val part1 = listOf("Tropa", "Bonde", "Galera", "Baba", "Racha", "Pelada", "Resenha", "Bar", "Os", "Só")
        val part2 = listOf("Pipoca", "Churrasco", "Pó de Arroz", "Fominha", "Perna de Pau", "Cervejeiros", "Boleiros")
        return "${part1.random()} ${part2.random()}"
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
