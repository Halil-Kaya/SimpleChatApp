package com.halilkaya.loginandregisterapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.halilkaya.loginandregisterapp.model.Kullanici
import com.halilkaya.loginandregisterapp.model.SohbetMesaj
import com.halilkaya.loginandregisterapp.model.SohbetOdasi
import com.halilkaya.loginandregisterapp.R
import com.halilkaya.loginandregisterapp.SohbetOdalariActivity
import kotlinx.android.synthetic.main.activity_sohbet_odalari.*
import java.text.SimpleDateFormat
import java.util.*

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

        btnSohbetOdasiOlustur.setOnClickListener {

            var rootLayout = (activity as SohbetOdalariActivity).rootLayout

            if(etSohbetAdi.text.isNotEmpty()){


                if(kullanicininSeviyesi > sbSeviyesi){

                    var ref = FirebaseDatabase.getInstance().reference

                    //sohbet odasi icin id aliyorum
                    var sohbetOdasiID = ref.child("sohbet_odasi").push().key


                    //yeni sohbet odasi olusturuyorum
                    var yeniSohbetOdasi = SohbetOdasi()
                    yeniSohbetOdasi.olusturan_id = FirebaseAuth.getInstance().currentUser?.uid!!
                    yeniSohbetOdasi.seviye = sbSeviyesi.toString()
                    yeniSohbetOdasi.sohbet_odasi_adi = etSohbetAdi.text.toString()
                    yeniSohbetOdasi.sohbet_odasi_id = sohbetOdasiID.toString()

                    //databese ekliyorm
                    ref.child("sohbet_odasi")
                        .child(sohbetOdasiID.toString())
                        .setValue(yeniSohbetOdasi)

                    //karsilama mesajı icin id olusturuyorum
                    var mesajID = ref.child("sohbet_odasi")
                        .child(sohbetOdasiID.toString())
                        .child("sohbet_odasi_mesajlari")
                        .push().key


                    //karsilama mesajını olusturuyorum
                    var karsilamaMesaji = SohbetMesaj()
                    karsilamaMesaji.time = getTarih()
                    karsilamaMesaji.mesaj = "hos geldin bravo six going dark"

                    //karsilama mesajini ekliyorum
                    ref.child("sohbet_odasi")
                        .child(sohbetOdasiID.toString())
                        .child("sohbet_odasi_mesajlari")
                        .child(mesajID.toString())
                        .setValue(karsilamaMesaji)

                    //ana ekrani guncelliyorum
                    (activity as SohbetOdalariActivity).init()

                    //ekrani kapatiyorum
                    dismiss()



                }else{


                    var snackbar = Snackbar.make(rootLayout,"Seviyen yetersiz",Snackbar.LENGTH_LONG)
                    snackbar.show()

                }


            }else{

                var snackbar = Snackbar.make(rootLayout,"Sohbet Odasinin adi bos olamaz",Snackbar.LENGTH_LONG)
                snackbar.show()

            }



        }



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



    fun getTarih():String{
        var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("tr"))
        return sdf.format(Date())
    }

}