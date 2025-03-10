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

    private lateinit var recyclerView: RecyclerView
    private lateinit var matchAdapter: MatchAdapter
    private lateinit var btnAdvance: Button
    private lateinit var txtChampion: TextView
    private lateinit var teams: List<Team>
    private var currentMatches: List<Match> = listOf()

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_knockout)

        recyclerView = findViewById(R.id.recyclerViewMatches)
        btnAdvance = findViewById(R.id.btnAdvance)
        txtChampion = findViewById(R.id.txtChampion)

        // Recupera times
        teams = (intent.getSerializableExtra("teams") as? ArrayList<Team>)?.toList() ?: emptyList()

        if (teams.isEmpty()) {
            Toast.makeText(this, "Nenhum time recebido!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentMatches = generateKnockoutMatches(teams)

        recyclerView.layoutManager = LinearLayoutManager(this)
        matchAdapter = MatchAdapter(currentMatches)
        recyclerView.adapter = matchAdapter

        btnAdvance.setOnClickListener { processResultsAndAdvance() }
    }

    private fun processResultsAndAdvance() {
        val updatedMatches = matchAdapter.getMatchesWithScores()

        // Verifica se algum placar está vazio
        if (updatedMatches.any { match ->
                match.score1 == null || match.score2 == null
            }) {
            Toast.makeText(this, "Preencha todos os placares!", Toast.LENGTH_SHORT).show()
            return
        }

        val nextRoundMatches = advanceToNextRound(updatedMatches)

        if (nextRoundMatches == null) {
            val champion = updatedMatches.firstOrNull()?.winner()
            if (champion != null) {
                txtChampion.text = "Campeão: ${champion.name}"
                recyclerView.visibility = View.GONE
                btnAdvance.visibility = View.GONE
            } else {
                Toast.makeText(this, "Erro ao definir campeão", Toast.LENGTH_SHORT).show()
            }
        } else {
            currentMatches = nextRoundMatches
            matchAdapter.updateMatches(currentMatches)
            txtChampion.text = ""
        }
    }
}