package com.osmardev.futebolterca

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlayerAdapter(
    private val players: MutableList<Player>,
    private val onRemovePlayer: (Player) -> Unit
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPlayerName: TextView = itemView.findViewById(R.id.tvPlayerName)
        val tvPlayerRating: TextView = itemView.findViewById(R.id.tvPlayerRating)
        val btnRemove: Button = itemView.findViewById(R.id.btnRemovePlayer)

        fun bind(player: Player) {
            tvPlayerName.text = player.name
            tvPlayerRating.text = "⭐ ${player.rating}"
            btnRemove.setOnClickListener { onRemovePlayer(player) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount(): Int = players.size
}

