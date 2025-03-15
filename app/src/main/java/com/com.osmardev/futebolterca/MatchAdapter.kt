package com.osmardev.futebolterca


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MatchAdapter(
    private var matches: List<Match>,
    private val onScoreChanged: () -> Unit
) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(matches[position])
    }

    override fun getItemCount(): Int = matches.size

    fun updateMatches(newMatches: List<Match>) {
        matches = newMatches
        notifyDataSetChanged()
    }

    inner class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTeam1: TextView = itemView.findViewById(R.id.tvTeam1)
        private val tvTeam2: TextView = itemView.findViewById(R.id.tvTeam2)
        private val etScore1: EditText = itemView.findViewById(R.id.etScore1)
        private val etScore2: EditText = itemView.findViewById(R.id.etScore2)

        fun bind(match: Match) {
            tvTeam1.text = match.team1.name
            tvTeam2.text = match.team2.name

            etScore1.setText(match.score1?.toString() ?: "")
            etScore2.setText(match.score2?.toString() ?: "")

            etScore1.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    match.score1 = etScore1.text.toString().toIntOrNull()
                    onScoreChanged()
                }
            }

            etScore2.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    match.score2 = etScore2.text.toString().toIntOrNull()
                    onScoreChanged()
                }
            }
        }
    }
}