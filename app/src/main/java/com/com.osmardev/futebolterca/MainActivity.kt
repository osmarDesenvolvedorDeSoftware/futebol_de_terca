package com.osmardev.futebolterca

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var btnGenerateTeams: Button
    private lateinit var btnStartChampionship: Button
    private lateinit var etPlayers: EditText
    private lateinit var etTeamCount: EditText
    private lateinit var spTournamentType: Spinner
    private lateinit var tvGeneratedTeams: TextView
    private lateinit var adView: AdView


    private var generatedTeams: List<Team> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}
        adView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)



        btnGenerateTeams = findViewById(R.id.btnGenerateTeams)
        btnStartChampionship = findViewById(R.id.btnStartChampionship)
        etPlayers = findViewById(R.id.etPlayers)
        etTeamCount = findViewById(R.id.etTeamCount)
        spTournamentType = findViewById(R.id.spTournamentType)
        tvGeneratedTeams = findViewById(R.id.tvGeneratedTeams)

        btnGenerateTeams.setOnClickListener { generateTeams() }
        btnStartChampionship.setOnClickListener { startTournament() }
    }

    private fun generateRandomTeamName(): String {
        val part1 = listOf("Tropa", "Bonde", "Galera", "Baba", "Racha", "Pelada", "Resenha", "Bar", "Os", "Só")
        val part2 = listOf("Pipoca", "Churrasco", "Pó de Arroz", "Fominha", "Perna de Pau", "Cervejeiros", "Boleiros")
        return "${part1.random()} ${part2.random()}"
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
}