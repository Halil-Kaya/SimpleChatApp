package com.halilkaya.loginandregisterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.halilkaya.loginandregisterapp.adapter.SohbetOdasiAdapter
import com.halilkaya.loginandregisterapp.fragments.DialogFragmentSohbetOdasiOlustur
import com.halilkaya.loginandregisterapp.model.SohbetMesaj
import com.halilkaya.loginandregisterapp.model.SohbetOdasi
import kotlinx.android.synthetic.main.activity_sohbet_odalari.*

class SohbetOdalariActivity : AppCompatActivity() {

    lateinit var tumSohbetOdalari:ArrayList<SohbetOdasi>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet_odalari)
        init()
        
    }

    fun init(){
        progresbarGoster()
        tumSohbetOdalariniGetir()

        fabtnYeniSohbetOdasi.setOnClickListener {
            var dialogFragmentSohbetOdasiOlustur = DialogFragmentSohbetOdasiOlustur()
            dialogFragmentSohbetOdasiOlustur.show(supportFragmentManager,"frag-sohbetOdasiOlustur")

        }
    }

    fun tumSohbetOdalariniGetir(){

        tumSohbetOdalari = ArrayList()

        var ref = FirebaseDatabase.getInstance().reference

        ref.child("sohbet_odasi").addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                for(tekSohbetOdasi in p0.children){

                    var sohbetOdasi = SohbetOdasi()
                    var tmpSohbetOdasi = (tekSohbetOdasi.getValue() as HashMap<String,Any>)

                    sohbetOdasi.seviye = tmpSohbetOdasi.get("seviye").toString()
                    sohbetOdasi.olusturan_id = tmpSohbetOdasi.get("olusturan_id").toString()
                    sohbetOdasi.sohbet_odasi_adi = tmpSohbetOdasi.get("sohbet_odasi_adi").toString()
                    sohbetOdasi.sohbet_odasi_id = tmpSohbetOdasi.get("sohbet_odasi_id").toString()


                    var sohbetOdasiMesajlari = ArrayList<SohbetMesaj>()

                    for(tekMesaj in tekSohbetOdasi.child("sohbet_odasi_mesajlari").children){

                        var tek_mesaj = tekMesaj.getValue(SohbetMesaj::class.java)

                        var tmpMesaj = SohbetMesaj()
                        tmpMesaj.adi = tek_mesaj?.adi.toString()
                        tmpMesaj.kullanici_id = tek_mesaj?.kullanici_id.toString()
                        tmpMesaj.mesaj = tek_mesaj?.mesaj.toString()
                        tmpMesaj.profil_resmi = tek_mesaj?.profil_resmi.toString()
                        tmpMesaj.time = tek_mesaj?.time.toString()

                        sohbetOdasiMesajlari.add(tmpMesaj)
                    }

                    sohbetOdasi.sohbet_odasi_mesajlari = sohbetOdasiMesajlari
                    tumSohbetOdalari.add(sohbetOdasi)

                }

                var myAdapter = SohbetOdasiAdapter(this@SohbetOdalariActivity,tumSohbetOdalari)
                rvSohbetOdalari.adapter = myAdapter
                rvSohbetOdalari.layoutManager = LinearLayoutManager(this@SohbetOdalariActivity,LinearLayoutManager.VERTICAL,false)
                progresbarGizle()

            }

        })



    }

    fun progresbarGoster(){
        myProgresBar.visibility = View.VISIBLE
    }

    fun progresbarGizle(){
        myProgresBar.visibility = View.INVISIBLE
    }


}