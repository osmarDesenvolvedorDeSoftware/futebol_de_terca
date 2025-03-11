package com.example.futebol_de_terca

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class StandingsAdapter(private var standings: List<Standing>) : RecyclerView.Adapter<StandingsAdapter.StandingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_standing, parent, false)
        return StandingViewHolder(view)
    }

    override fun onBindViewHolder(holder: StandingViewHolder, position: Int) {
        val standing = standings[position]
        holder.bind(standing, position)
    }

    override fun getItemCount(): Int = standings.size

    fun updateStandings(newStandings: List<Standing>) {
        standings = newStandings
        notifyDataSetChanged()
    }

    class StandingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTeam: TextView = itemView.findViewById(R.id.tvTeam)
        private val tvPoints: TextView = itemView.findViewById(R.id.tvPoints)
        private val tvWins: TextView = itemView.findViewById(R.id.tvWins)
        private val tvDraws: TextView = itemView.findViewById(R.id.tvDraws)
        private val tvLosses: TextView = itemView.findViewById(R.id.tvLosses)

        fun bind(standing: Standing, position: Int) {
            tvTeam.text = standing.team.name
            tvPoints.text = "Pontos: ${standing.points}"
            tvWins.text = "Vitórias: ${standing.wins}"
            tvDraws.text = "Empates: ${standing.draws}"
            tvLosses.text = "Derrotas: ${standing.losses}"


            val context = itemView.context
            val backgroundColor = when (position) {
                0 -> ContextCompat.getColor(context, R.color.gold)
                1 -> ContextCompat.getColor(context, R.color.silver)
                2 -> ContextCompat.getColor(context, R.color.bronze)
                else -> ContextCompat.getColor(context, R.color.background)
            }
            itemView.setBackgroundColor(backgroundColor)
        }
    }
}
