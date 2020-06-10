package com.halilkaya.loginandregisterapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.halilkaya.loginandregisterapp.Model.Kullanici
import com.halilkaya.loginandregisterapp.R

class DialogFragmentSohbetOdasiOlustur : DialogFragment() {

    lateinit var etSohbetAdi:EditText
    lateinit var sbSeviye:SeekBar
    lateinit var tvSeviye:TextView
    lateinit var btnSohbetOdasiOlustur:Button
    lateinit var tvKullaniciSeviyesi:TextView
    var kullanicininSeviyesi = 0
    var sbSeviyesi = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.dialog_fragment_sohbet_odasi_olustur,container,false)

        etSohbetAdi = view.findViewById(R.id.etSohbetAdi)
        sbSeviye = view.findViewById(R.id.sbSeviye)
        tvSeviye = view.findViewById(R.id.tvSeviye)
        btnSohbetOdasiOlustur = view.findViewById(R.id.btnSohbetOdasiOlustur)
        tvKullaniciSeviyesi = view.findViewById(R.id.tvKullaniciSeviyesi)
        init()



        return view
    }

    fun init(){

        kullanicininSeviyesiniOgren()

        sbSeviye.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvSeviye.setText("$progress")
                sbSeviyesi = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })


    }

    fun kullanicininSeviyesiniOgren(){

        var kullanici = FirebaseAuth.getInstance().currentUser

        var ref = FirebaseDatabase.getInstance().reference

        var sorgu = ref
            .child("kullanici")
            .orderByKey()
            .equalTo(kullanici?.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(p0: DataSnapshot) {

                    for(user in p0.children){
                        kullanicininSeviyesi = user.getValue(Kullanici::class.java)?.seviye!!.toInt()
                        tvKullaniciSeviyesi.setText("Sizin Seviyeniz: ${kullanicininSeviyesi}")

                    }

                }

            })


    }

}