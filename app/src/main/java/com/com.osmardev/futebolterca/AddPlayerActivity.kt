package com.osmardev.futebolterca

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddPlayerActivity : AppCompatActivity() {

    private lateinit var etPlayerName: EditText
    private lateinit var etPlayerRating: EditText
    private lateinit var btnAddPlayer: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_player)

        etPlayerName = findViewById(R.id.etPlayerName)
        etPlayerRating = findViewById(R.id.etPlayerRating)
        btnAddPlayer = findViewById(R.id.btnAddPlayer)

        btnAddPlayer.setOnClickListener {
            val name = etPlayerName.text.toString().trim()
            val rating = etPlayerRating.text.toString().toIntOrNull()

            if (name.isEmpty() || rating == null || rating !in 1..5) {
                Toast.makeText(this, "Preencha corretamente o nome e a nota (1 a 5).", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val player = Player(name, rating)
            val resultIntent = Intent()
            resultIntent.putExtra("newPlayer", player)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
