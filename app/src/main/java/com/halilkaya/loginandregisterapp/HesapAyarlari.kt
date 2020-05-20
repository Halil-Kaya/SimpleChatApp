package com.halilkaya.loginandregisterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_hesap_ayarlari.*

class HesapAyarlari : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hesap_ayarlari)

        tvMailSifreGuncelle.setOnClickListener {

            gizliConstrainLayout.visibility = View.VISIBLE

        }

    }
}
