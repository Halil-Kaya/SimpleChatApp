package com.halilkaya.loginandregisterapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.halilkaya.loginandregisterapp.R
import com.halilkaya.loginandregisterapp.model.Kullanici
import com.halilkaya.loginandregisterapp.model.SohbetOdasi
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tek_sohbet_odasi.view.*

class SohbetOdasiAdapter(var myActivity:Context,var tumSohbetOdalari:ArrayList<SohbetOdasi>) : RecyclerView.Adapter<SohbetOdasiAdapter.MyViewHolder>(){





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SohbetOdasiAdapter.MyViewHolder {

        var inflater = LayoutInflater.from(myActivity)
        var view = inflater.inflate(R.layout.tek_sohbet_odasi,parent,false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tumSohbetOdalari.size
    }

    override fun onBindViewHolder(holder: SohbetOdasiAdapter.MyViewHolder, position: Int) {

        var oAnKiSohbetOdasi = tumSohbetOdalari.get(position)
        holder.setData(oAnKiSohbetOdasi,position)

    }



    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var tek_satir_sohbet_odasi = itemView as CardView
        var imgOlusturaninProfilResmi = tek_satir_sohbet_odasi.imgOlusturaninProfilResmi
        var tvSohbetOdasiAdi = tek_satir_sohbet_odasi.tvSohbetOdasiAdi
        var tvSohbetOdasiOlusturaninAdi = tek_satir_sohbet_odasi.tvSohbetOdasiOlusturaninAdi
        var tvSohbetOdasiMesajSayisi = tek_satir_sohbet_odasi.tvSohbetOdasiMesajSayisi
        var btnSohbetOdasiniSil = tek_satir_sohbet_odasi.btnSohbetOdasiniSil
        var tvSohbetOdasiSeviye = tek_satir_sohbet_odasi.tvSohbetOdasiSeviye

        fun setData(oAnKiSohbetOdasi:SohbetOdasi, position: Int){
            init()
            tvSohbetOdasiAdi.setText(oAnKiSohbetOdasi.sohbet_odasi_adi)
            tvSohbetOdasiMesajSayisi.setText(oAnKiSohbetOdasi.sohbet_odasi_mesajlari?.size.toString())
            tvSohbetOdasiSeviye.setText(oAnKiSohbetOdasi.seviye)

            //simdi ise yapmam gereken sohbet odasindaki kullanici id yi kullanarak kullaniiciyi bulup
            //onun profil resmini ve adini bastirmak

            var ref = FirebaseDatabase.getInstance().reference

            ref.child("kullanici")
                .orderByKey()
                .equalTo(oAnKiSohbetOdasi.olusturan_id).addListenerForSingleValueEvent(object : ValueEventListener{

                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        for(user in p0.children){

                            var profilResmiUrl = user.getValue(Kullanici::class.java)?.profil_resmi
                            var kullaniciAdi = user.getValue(Kullanici::class.java)?.isim

                            Picasso.get().load(profilResmiUrl).resize(56,56).into(imgOlusturaninProfilResmi)
                            tvSohbetOdasiOlusturaninAdi.setText(kullaniciAdi)


                        }

                    }


                })



        }

        fun init(){

            //o sohbet odasini aciyorum
            tek_satir_sohbet_odasi.setOnClickListener{

            }


            //sohbet odasini siliyorum
            btnSohbetOdasiniSil.setOnClickListener {

            }


        }


    }


}