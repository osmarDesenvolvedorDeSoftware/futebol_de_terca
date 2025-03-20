package com.osmardev.futebolterca

import android.animation.ObjectAnimator
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ChampionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_champion)

        val championName = intent.getStringExtra("championName") ?: "Desconhecido"

        val tvChampionName = findViewById<TextView>(R.id.tvChampionName)
        tvChampionName.text = championName

        showCelebration()
    }

    private fun showCelebration() {
        val confettiView = findViewById<ImageView>(R.id.imgConfetti)
        confettiView.visibility = ImageView.VISIBLE

        val animation = ObjectAnimator.ofFloat(confettiView, "translationY", -500f, 1000f)
        animation.duration = 3000
        animation.repeatCount = ObjectAnimator.INFINITE
        animation.start()
    }
}
