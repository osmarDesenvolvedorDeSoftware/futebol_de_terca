package com.osmardev.futebolterca

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class MainActivity : AppCompatActivity() {

    private lateinit var btnGenerateTeams: Button
    private lateinit var btnStartChampionship: Button
    private lateinit var btnAddPlayer: Button
    private lateinit var recyclerViewPlayers: RecyclerView
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var etTeamCount: EditText
    private lateinit var etPlayersPerTeam: EditText
    private lateinit var spTournamentType: MaterialAutoCompleteTextView
    private lateinit var tvGeneratedTeams: TextView

    private val playersList = mutableListOf<Player>()
    private var generatedTeams: List<Team> = emptyList()

    companion object {
        private const val REQUEST_ADD_PLAYER = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerViewPlayers = findViewById(R.id.recyclerViewPlayers)
        btnGenerateTeams = findViewById(R.id.btnGenerateTeams)
        btnStartChampionship = findViewById(R.id.btnStartChampionship)
        btnAddPlayer = findViewById(R.id.btnAddPlayer)
        etTeamCount = findViewById(R.id.etTeamCount)
        etPlayersPerTeam = findViewById(R.id.etPlayersPerTeam)
        spTournamentType = findViewById(R.id.spTournamentType)
        tvGeneratedTeams = findViewById(R.id.tvGeneratedTeams)

        val tournamentTypes = arrayOf("Mata-Mata", "Pontos Corridos")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tournamentTypes)
        spTournamentType.setAdapter(adapter)

        spTournamentType.setOnClickListener { spTournamentType.showDropDown() }

        playerAdapter = PlayerAdapter(playersList) { player ->
            removePlayer(player)
        }
        recyclerViewPlayers.layoutManager = LinearLayoutManager(this)
        recyclerViewPlayers.adapter = playerAdapter

        btnGenerateTeams.setOnClickListener {
            btnGenerateTeams.isEnabled = false
            btnGenerateTeams.text = "Gerando Times..."
            Handler(Looper.getMainLooper()).postDelayed({
                generateTeams()
                btnGenerateTeams.isEnabled = true
                btnGenerateTeams.text = "Gerar Times ⚽"
            }, 1000)
        }
        btnStartChampionship.setOnClickListener { startTournament() }
        btnAddPlayer.setOnClickListener { openAddPlayerScreen() }
    }

    private fun openAddPlayerScreen() {
        val intent = Intent(this, AddPlayerActivity::class.java)
        startActivityForResult(intent, REQUEST_ADD_PLAYER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_PLAYER && resultCode == Activity.RESULT_OK) {
            val newPlayer = data?.getParcelableExtra<Player>("newPlayer")
            newPlayer?.let {
                playersList.add(it)
                updatePlayersUI()
                Snackbar.make(recyclerViewPlayers, "${it.name} foi adicionado! ✅", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePlayersUI() {
        playerAdapter.notifyDataSetChanged()
    }

    private fun removePlayer(player: Player) {
        playersList.remove(player)
        updatePlayersUI()
        showToast("${player.name} foi removido!")
    }

    private fun generateTeams() {
        val players = playersList
        val teamCount = etTeamCount.text.toString().toIntOrNull() ?: 0

        if (players.isEmpty() || teamCount == 0) {
            Snackbar.make(btnGenerateTeams, "Preencha os campos corretamente!", Snackbar.LENGTH_LONG).show()
            return
        }

        if (players.size < teamCount * 2) {
            Snackbar.make(btnGenerateTeams, "Faltam jogadores para formar os times!", Snackbar.LENGTH_LONG).show()
            return
        }

        generatedTeams = generateBalancedTeams(players, teamCount)
        displayGeneratedTeams()
        btnStartChampionship.isEnabled = true
    }

    private fun displayGeneratedTeams() {
        val formattedTeams = StringBuilder()
        generatedTeams.forEach { team ->
            formattedTeams.append("<b>⚽ ${team.name}</b><br>")
            team.players.forEach { player ->
                formattedTeams.append("➜ ${player.name} (${player.rating}★)<br>")
            }
            formattedTeams.append("<br>")
        }
        tvGeneratedTeams.text = Html.fromHtml(formattedTeams.toString(), Html.FROM_HTML_MODE_LEGACY)
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
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun generateBalancedTeams(players: List<Player>, teamCount: Int): List<Team> {
        val sortedPlayers = players.sortedByDescending { it.rating }
        val teams = MutableList(teamCount) { mutableListOf<Player>() }

        sortedPlayers.forEachIndexed { index, player ->
            teams[index % teamCount].add(player)
        }

        val uniqueNames = mutableSetOf<String>()
        return teams.map {
            var teamName: String
            do {
                teamName = generateRandomTeamName()
            } while (uniqueNames.contains(teamName))
            uniqueNames.add(teamName)
            Team(teamName, ArrayList(it))
        }
    }

    private fun generateRandomTeamName(): String {
        val part1 = listOf("Tropa", "Bonde", "Galera", "Baba", "Racha", "Pelada", "Resenha", "Bar", "Os", "Só")
        val part2 = listOf("Pipoca", "Churrasco", "Pó de Arroz", "Fominha", "Perna de Pau", "Cervejeiros", "Boleiros")

        return "${part1.random()} ${part2.random()}"
    }
}