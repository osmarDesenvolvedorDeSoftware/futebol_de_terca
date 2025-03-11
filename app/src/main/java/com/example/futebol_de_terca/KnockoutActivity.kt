package com.example.futebol_de_terca

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class KnockoutActivity : AppCompatActivity() {
    private lateinit var matches: List<Match>
    private lateinit var tournamentType: String
    private lateinit var matchAdapter: MatchAdapter
    private lateinit var btnConfirmPenalty: Button
    private lateinit var txtPenaltyResult: TextView
    private var pendingMatches: MutableList<Match> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_knockout)

        val btnAdvance: Button = findViewById(R.id.btnAdvance)
        btnAdvance.setOnClickListener {
            if (pendingMatches.isEmpty()) {
                advanceToNextRound()
            } else {
                showStatusMessage("Resolva todos os pênaltis antes de avançar!")
            }
        }

        btnConfirmPenalty = findViewById(R.id.btnConfirmPenalty)
        txtPenaltyResult = findViewById(R.id.txtPenaltyResult)
        btnConfirmPenalty.visibility = View.GONE
        txtPenaltyResult.visibility = View.GONE

        btnConfirmPenalty.setOnClickListener {
            while (pendingMatches.isNotEmpty()) {
                val match = pendingMatches.removeAt(0)
                confirmPenaltyResult(match)
            }
            btnConfirmPenalty.visibility = View.GONE
            findViewById<Button>(R.id.btnAdvance).isEnabled = true
        }

        val teams = intent.getSerializableExtra("teams") as? ArrayList<Team> ?: arrayListOf()
        tournamentType = intent.getStringExtra("tournamentType") ?: "Mata-Mata"

        if (teams.isNotEmpty()) {
            startKnockoutTournament(teams)
        }
    }

    private fun startKnockoutTournament(teams: List<Team>) {
        matches = generateKnockoutMatches(teams)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewMatches)
        recyclerView.layoutManager = LinearLayoutManager(this)

        matchAdapter = MatchAdapter(matches) {
            matches.forEach { checkMatchResult(it) }
        }
        recyclerView.adapter = matchAdapter
    }

    private fun advanceToNextRound() {
        if (matches.any { it.score1 == null || it.score2 == null }) {
            showStatusMessage("Preencha todos os placares antes de avançar!")
            return
        }

        if (pendingMatches.isNotEmpty()) {
            showStatusMessage("Resolva todos os pênaltis antes de avançar!")
            return
        }

        val winners = matches.mapNotNull { match ->
            if (match.score1!! > match.score2!!) match.team1 else match.team2
        }

        if (winners.size == 1) {

            findViewById<TextView>(R.id.txtChampion).text = "Campeão: ${winners.first().name} 🏆"

            findViewById<Button>(R.id.btnAdvance).visibility = View.GONE
        } else {
            showStatusMessage("Avançando para a próxima rodada...")
            matches = generateKnockoutMatches(winners)
            setupRecyclerView()
        }
    }

    private fun checkMatchResult(match: Match) {
        if (match.score1 != null && match.score2 != null && match.score1 == match.score2) {
            runOnUiThread {
                showStatusMessage("Empate! Resolva os pênaltis antes de avançar.")
                btnConfirmPenalty.visibility = View.VISIBLE
                txtPenaltyResult.visibility = View.VISIBLE
                txtPenaltyResult.text = "Pênaltis em andamento..."
                pendingMatches.add(match)
                findViewById<Button>(R.id.btnAdvance).isEnabled = false
            }
        }
    }

    private fun confirmPenaltyResult(match: Match) {
        println("DEBUG: Resolvendo pênaltis para ${match.team1.name} vs ${match.team2.name}")
        if (match.score1 == null || match.score2 == null) {
            showStatusMessage("Erro: Placar inválido para pênaltis!")
            return
        }

        val winner = if ((0..1).random() == 0) match.team1 else match.team2
        val resultMessage = "${winner.name} venceu nos pênaltis! ✅"

        runOnUiThread {
            showStatusMessage(resultMessage)
            match.score1 = (match.score1 ?: 0) + if (winner == match.team1) 1 else 0
            match.score2 = (match.score2 ?: 0) + if (winner == match.team2) 1 else 0
        }
    }

    private fun showStatusMessage(message: String) {
        runOnUiThread {
            txtPenaltyResult.text = message
            txtPenaltyResult.visibility = View.VISIBLE
        }
    }
}