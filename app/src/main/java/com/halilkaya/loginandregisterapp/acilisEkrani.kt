package com.halilkaya.loginandregisterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_acilis_ekrani.*

class acilisEkrani : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acilis_ekrani)

        btnGirisYap.setOnClickListener {

            var intent = Intent(this,GirisEkrani::class.java)
            startActivity(intent)

        }

        btnKaydol.setOnClickListener {

            var intent = Intent(this,KayitEkrani::class.java)
            startActivity(intent)

        }


    }
}
