package com.halilkaya.loginandregisterapp.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.halilkaya.loginandregisterapp.MainActivity
import com.halilkaya.loginandregisterapp.MesajlarActivity
import com.halilkaya.loginandregisterapp.R
import com.halilkaya.loginandregisterapp.model.SohbetOdasi
import java.util.*
import kotlin.collections.HashMap

class MyFirebaseMessagingService : FirebaseMessagingService() {

    var ref = FirebaseDatabase.getInstance().reference

    var okunmayiBekleyenMesajSayisi = 0

    override fun onMessageReceived(p0: RemoteMessage) {


        //eger mesaklar ekrani kapaliysa bildirim atmaliyim
        if(!activityAcikMi()){



        var data = p0.data
        var baslik = data.get("baslik")
        var icerik = data.get("icerik")
        var bildirimTuru = data.get("bildirim_turu")
        var sohbet_odasi_id = data.get("sohbet_odasi_id")

        println("--------------------")
        println("baslik: ${baslik}")
        println("icerik: ${icerik}")
        println("bildirimTuru: ${bildirimTuru}")
        println("sohbet_odasi_id: ${sohbet_odasi_id}")
        println("--------------------")


            // kac tane okunmamis mesaji oldugunu kontrol etmem lazim
            //o yuzden o sohbet odasindaki kullanicilarin okunmus mesaj sayisini kontrol ediyorum
            FirebaseDatabase.getInstance().reference
                .child("sohbet_odasi")
                .orderByKey()
                .equalTo(sohbet_odasi_id)
                .addListenerForSingleValueEvent(object : ValueEventListener{

                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        var tekSohbetOdasi = p0.children.iterator().next()

                        var oAnkiSohbetOdasi = SohbetOdasi()
                        //icinde baska bir dugum oldugu icin dogrudan alamiyorum o yuzden hashmap e atmam gerekiyor

                        var nesneMap = (tekSohbetOdasi.getValue() as HashMap<String,Object>)

                        oAnkiSohbetOdasi.sohbet_odasi_id = nesneMap.get("sohbet_odasi_id").toString()
                        oAnkiSohbetOdasi.sohbet_odasi_adi = nesneMap.get("sohbet_odasi_adi").toString()
                        oAnkiSohbetOdasi.olusturan_id = nesneMap.get("olusturan_id").toString()
                        oAnkiSohbetOdasi.seviye = nesneMap.get("seviye").toString()

                        var gorulenMesajSayisi = tekSohbetOdasi
                            .child("sohbet_odasindaki_kullanicilar")
                            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                            .child("okunmamis_mesaj_sayisi")
                            .getValue().toString().toInt()

                        var toplamMesajSayisi = tekSohbetOdasi.child("sohbet_odasi_mesajlari").childrenCount.toInt()

                        okunmayiBekleyenMesajSayisi = toplamMesajSayisi - gorulenMesajSayisi

                        //bildirimi gonderiyorum
                        bildirimGonder(baslik,icerik,oAnkiSohbetOdasi)


                    }

                })



        }


    }

    fun bildirimGonder(baslik:String?,icerik:String?,oAnKiSohbetOdasi: SohbetOdasi){


        var bildirimID = notificationIDolustur(oAnKiSohbetOdasi.sohbet_odasi_id)

        var pendingIntent = Intent(this, MainActivity::class.java)
        pendingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("sohbet_odasi_id",oAnKiSohbetOdasi.sohbet_odasi_id)

        var bildirimPendingIntent = PendingIntent.getActivity(this,100,pendingIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        var builder = NotificationCompat.Builder(this,oAnKiSohbetOdasi.sohbet_odasi_adi)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(oAnKiSohbetOdasi.sohbet_odasi_adi+"odasindan " +baslik)
            .setAutoCancel(true)
            .setSubText("okunmayi bekleyen mesaj sayisi: "+okunmayiBekleyenMesajSayisi)
            .setOnlyAlertOnce(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(icerik))
            .setContentIntent(bildirimPendingIntent)

        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(bildirimID,builder.build())

    }

    fun notificationIDolustur(sohbetOdasiID:String):Int{

        var id = 0

        for(i in 4..8){

            id+=sohbetOdasiID[i].toInt()

        }


        return id
    }

    fun activityAcikMi():Boolean{

        if(MesajlarActivity.activityAcikMi){
            return true
        }

        return false

    }

    override fun onNewToken(p0: String) {

        var newToken = p0

        ref.child("kullanici")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .child("mesaj_token")
            .setValue(newToken)



    }



}