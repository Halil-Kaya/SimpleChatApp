package com.halilkaya.loginandregisterapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.halilkaya.loginandregisterapp.R
import com.halilkaya.loginandregisterapp.model.Kullanici
import com.halilkaya.loginandregisterapp.model.SohbetMesaj
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.mesaj_gelen.view.*

class MesajlarAdapter(var myActivity:Context,var mesajlar:ArrayList<SohbetMesaj>) : RecyclerView.Adapter<MesajlarAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var infater = LayoutInflater.from(myActivity)

        var view:View? = null

        if(viewType == 100){
            view = infater.inflate(R.layout.mesaj_gonderen,parent,false)
        }else if(viewType == 200){
            view = infater.inflate(R.layout.mesaj_gelen,parent,false)
        }
        return MyViewHolder(view!!)
    }

    override fun getItemViewType(position: Int): Int {

        if(mesajlar.get(position).kullanici_id == FirebaseAuth.getInstance().currentUser!!.uid){
            return 100
        }else{
            return 200
        }
    }


    override fun getItemCount(): Int {
        return mesajlar.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var oAnKiMesaj = mesajlar.get(position)
        holder.setData(oAnKiMesaj,position)
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var mesaj_layout = itemView as ConstraintLayout
        var imgMesajProfilResmi = mesaj_layout.imgMesajProfilResmi
        var tvMesaj = mesaj_layout.tvMesaj
        var tvTarih = mesaj_layout.tvTarih
        var tvGondereninAdi = mesaj_layout.tvGondereninAdi

        fun setData(oAnKiMesaj:SohbetMesaj,position:Int){
            tvMesaj.setText(oAnKiMesaj.mesaj)
            tvTarih.setText(oAnKiMesaj.time)

            var ref = FirebaseDatabase.getInstance().reference

            ref.child("kullanici")
                .orderByKey()
                .equalTo(oAnKiMesaj.kullanici_id.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        for(user in p0.children){
                            var kullanici = user.getValue(Kullanici::class.java)
                            tvGondereninAdi.setText(kullanici?.isim)
                            Picasso.get().load(kullanici?.profil_resmi).resize(48,48).into(imgMesajProfilResmi)
                        }

                    }

                })



        }


    }




}