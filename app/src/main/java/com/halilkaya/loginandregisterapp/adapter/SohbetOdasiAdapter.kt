package com.halilkaya.loginandregisterapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.halilkaya.loginandregisterapp.MesajlarActivity
import com.halilkaya.loginandregisterapp.R
import com.halilkaya.loginandregisterapp.SohbetOdalariActivity
import com.halilkaya.loginandregisterapp.model.Kullanici
import com.halilkaya.loginandregisterapp.model.SohbetOdasi
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_sohbet_odalari.*
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

        var tek_satir_sohbet_odasi = itemView as ConstraintLayout
        var imgOlusturaninProfilResmi = tek_satir_sohbet_odasi.imgOlusturaninProfilResmi
        var tvSohbetOdasiAdi = tek_satir_sohbet_odasi.tvSohbetOdasiAdi
        var tvSohbetOdasiOlusturaninAdi = tek_satir_sohbet_odasi.tvSohbetOdasiOlusturaninAdi
        var tvSohbetOdasiMesajSayisi = tek_satir_sohbet_odasi.tvSohbetOdasiMesajSayisi
        var btnSohbetOdasiniSil = tek_satir_sohbet_odasi.btnSohbetOdasiniSil
        var tvSohbetOdasiSeviye = tek_satir_sohbet_odasi.tvSohbetOdasiSeviye

        fun setData(oAnKiSohbetOdasi:SohbetOdasi, position: Int){
            init(oAnKiSohbetOdasi)
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

        fun init(oAnKiSohbetOdasi:SohbetOdasi){
            var ref = FirebaseDatabase.getInstance().reference

            //tiklanilan sohbet odasini aciyorum
            tek_satir_sohbet_odasi.setOnClickListener{

                kullaniciyiSohbetOdasinaKaydet(oAnKiSohbetOdasi)

                var intent = Intent(tek_satir_sohbet_odasi.context,MesajlarActivity::class.java)
                intent.putExtra("sohbetID",oAnKiSohbetOdasi.sohbet_odasi_id)
                intent.putExtra("sohbetOdasiAdi",oAnKiSohbetOdasi.sohbet_odasi_adi)
                intent.putExtra("sohbetOdasiSeviye",oAnKiSohbetOdasi.seviye)
                (myActivity as SohbetOdalariActivity).startActivity(intent)

            }

            //sohbet odasini siliyorum
            btnSohbetOdasiniSil.setOnClickListener {

                if(oAnKiSohbetOdasi.olusturan_id.equals(FirebaseAuth.getInstance().currentUser?.uid)){

                    var myAlertDialog = AlertDialog.Builder(tek_satir_sohbet_odasi.context)
                    myAlertDialog.setTitle("Sohbet Odasi Silinsin Mi")

                    myAlertDialog.setPositiveButton("Evet Sil",object : DialogInterface.OnClickListener{

                        override fun onClick(dialog: DialogInterface?, which: Int) {

                            ref.child("sohbet_odasi")
                                .child(oAnKiSohbetOdasi.sohbet_odasi_id)
                                .removeValue()

                            tumSohbetOdalari.remove(oAnKiSohbetOdasi)
                            notifyDataSetChanged()
                        }

                    })
                    myAlertDialog.setNegativeButton("Hayir Silme", object : DialogInterface.OnClickListener{

                        override fun onClick(dialog: DialogInterface?, which: Int) {
                        }

                    })

                    myAlertDialog.show()

                }else{

                    var rootLayout = (myActivity as SohbetOdalariActivity).rootLayout
                    var snackbar = Snackbar.make(rootLayout,"Odayi sen olusturmadin", Snackbar.LENGTH_SHORT)
                    snackbar.show()

                }
            }

        }

        fun kullaniciyiSohbetOdasinaKaydet(oAnKiSohbetOdasi:SohbetOdasi){


            var ref = FirebaseDatabase.getInstance().reference
            ref.child("sohbet_odasi")
                .child(oAnKiSohbetOdasi.sohbet_odasi_id.toString())
                .child("sohbet_odasindaki_kullanicilar")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("okunmamis_mesaj_sayisi")
                .setValue(oAnKiSohbetOdasi.sohbet_odasi_mesajlari?.size.toString())


        }


    }


}