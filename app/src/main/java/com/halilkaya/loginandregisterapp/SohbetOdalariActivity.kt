package com.halilkaya.loginandregisterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.halilkaya.loginandregisterapp.Fragments.DialogFragmentSohbetOdasiOlustur
import kotlinx.android.synthetic.main.activity_sohbet_odalari.*

class SohbetOdalariActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet_odalari)
        init()


    }

    fun init(){
        fabtnYeniSohbetOdasi.setOnClickListener {
            var dialogFragmentSohbetOdasiOlustur = DialogFragmentSohbetOdasiOlustur()
            dialogFragmentSohbetOdasiOlustur.show(supportFragmentManager,"frag-sohbetOdasiOlustur")
        }
    }

}