package com.example.futebol_de_terca

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var btnGenerateTeams: Button
    private lateinit var btnStartChampionship: Button
    private lateinit var etPlayers: EditText
    private lateinit var etTeamCount: EditText
    private lateinit var etPlayersPerTeam: EditText
    private lateinit var spTournamentType: Spinner
    private lateinit var tvGeneratedTeams: TextView

    private var generatedTeams: List<Team> = emptyList()

    private val teamNames = listOf(
        "Unidos do Gole", "Só Canelas", "Tropa do Gás", "Sem Freio FC", "Os Cansados",
        "Raça Ruim", "Peladeiros FC", "Só Resenha", "Baba da Esquina", "Chuteira Furada",
        "Vai ou Racha", "Só Toquinho", "Real Madruga", "Ajax da Favela", "Tropa do Pó de Arroz",
        "Os Boleiros", "Cai Dentro FC", "Meia Boca Juniors", "Caneludos FC", "Bar Sem Lona",
        "Borussia da Quebrada", "Avassaladores", "Bico Certo", "Sem Filtro FC"
    )

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

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.tournament_types,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spTournamentType.adapter = adapter
    }

    private fun generateTeams() {
        val playersInput = etPlayers.text.toString().trim()
        val teamCount = etTeamCount.text.toString().toIntOrNull() ?: 0
        val playersPerTeam = etPlayersPerTeam.text.toString().toIntOrNull() ?: 0

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

        val availableNames = teamNames.shuffled().take(teamCount)

        generatedTeams = List(teamCount) { index ->
            val start = index * playersPerTeam
            val end = start + playersPerTeam
            Team(availableNames[index], ArrayList(players.subList(start, end)))
        }

        tvGeneratedTeams.text = generatedTeams.joinToString("\n\n") { team ->
            "${team.name}: ${team.players.joinToString(", ")}"
        }

        btnStartChampionship.isEnabled = true
        showToast("Times gerados!")

        val mediaPlayer = MediaPlayer.create(this, R.raw.torcida)
        mediaPlayer.start()
    }

    private fun startTournament() {
        if (generatedTeams.isEmpty()) {
            showToast("Erro: Nenhum time gerado!")
            return
        }

        println("Times enviados para o campeonato: ${generatedTeams.joinToString { it.name }}")

        val intent = when (spTournamentType.selectedItem.toString()) {
            "Mata-Mata" -> Intent(this, KnockoutActivity::class.java)
            "Pontos Corridos" -> Intent(this, LeagueActivity::class.java)
            else -> {
                showToast("Modo não implementado")
                return
            }
        }

        intent.putExtra("teams", ArrayList(generatedTeams))
        intent.putExtra("tournamentType", spTournamentType.selectedItem.toString())

        startActivity(intent)
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
