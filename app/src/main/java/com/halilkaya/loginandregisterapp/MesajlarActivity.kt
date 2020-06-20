package com.halilkaya.loginandregisterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.halilkaya.loginandregisterapp.adapter.MesajlarAdapter
import com.halilkaya.loginandregisterapp.model.FCMModel
import com.halilkaya.loginandregisterapp.model.Kullanici
import com.halilkaya.loginandregisterapp.model.SohbetMesaj
import com.halilkaya.loginandregisterapp.services.FCMInterface
import kotlinx.android.synthetic.main.activity_mesajlar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class MesajlarActivity : AppCompatActivity() {

    var myAuthStateListener:FirebaseAuth.AuthStateListener? = null
    var sohbetOdasiID:String? = ""

    var myAdapter : MesajlarAdapter? = null

    var myHashSet:HashSet<String>? = null

    companion object{
        var activityAcikMi = false
    }

    //databesimde mesajlar kismini referans alicak boylece o kisimda bir degisiklik oldugunda
    //yani yeni mesaj geldiginde burasi tetiklenecek
    lateinit var myReference:DatabaseReference

    var tumMesajlar:ArrayList<SohbetMesaj>? = null

    val BASE_URL = "https://fcm.googleapis.com/fcm/"
    var SERVER_KEY = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesajlar)
        baslatFirebaseAuthStateListener()
        init()
        baslatMesajListener()


    }
    fun init(){

        serverKeyiAl()

        sohbetOdasiID = intent.getStringExtra("sohbetID")
        var sohbetOdasiAdi = intent.getStringExtra("sohbetOdasiAdi")
        var sohbetOdasiSeviye = intent.getStringExtra("sohbetOdasiSeviye")
        myToolbar.setNavigationIcon(R.drawable.trashicon)
        myToolbar.setTitle(sohbetOdasiAdi)
        myToolbar.setSubtitle(sohbetOdasiSeviye)


        myToolbar.setNavigationOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                onBackPressed()
            }

        })

        btnMesajGonder.setOnClickListener {

            if(!etMesaj.text.isNullOrEmpty()){

                var ref = FirebaseDatabase.getInstance().reference

                var sohbetMesaj = SohbetMesaj()
                sohbetMesaj.time = getTarih()
                sohbetMesaj.mesaj = etMesaj.text.toString()
                sohbetMesaj.kullanici_id = FirebaseAuth.getInstance().currentUser?.uid.toString()

                var mesajID = ref.child("sohbet_odasi")
                    .child(sohbetOdasiID.toString())
                    .child("sohbet_odasi_mesajlari").push().key

                ref.child("sohbet_odasi")
                    .child(sohbetOdasiID.toString())
                    .child("sohbet_odasi_mesajlari")
                    .child(mesajID.toString())
                    .setValue(sohbetMesaj)


                var mesaj = etMesaj.text.toString()
                etMesaj.text.toString()

                //retrofiti olusturuyorum
                var retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                //retrofitin interface kısmını ekliyorum bu kısımları baglantının parametlerini eklemek icin kullanılıyor ayrıca
                //bu kısmı kurallar gibi dusunebilirsin
                var myInterface = retrofit.create(FCMInterface::class.java)

                //benim baglantimda post yaparken bir kac parametre girmem girikiyor o kisimlari ekliyorum
                var headers = HashMap<String,String>()
                headers.put("Content-Type","application/json")
                headers.put("Authorization","key=${SERVER_KEY}")

                FirebaseDatabase.getInstance().reference
                    .child("sohbet_odasi")
                    .child(sohbetOdasiID.toString())
                    .child("sohbet_odasindaki_kullanicilar")
                    .orderByKey().addListenerForSingleValueEvent(object : ValueEventListener{

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            for(user in p0.children){

                                var id = user.key

                                //mesaj atan kisiye bildirim gitmesin diye
                                if(!id.equals(FirebaseAuth.getInstance().currentUser?.uid.toString())){

                                    //simdi de kullanicilarin token larini bulmam gerekiyor ona gore cihazlari bulacak!
                                    //bu yuzden kullanicilari teker teker bulup tokenini bulup bildirim aticak
                                    //NOT BU YONTEM MANTIKLI BIR YONTEM DEGIL ASLINDA COK FAZLA KAYNAK TUKETIYOR

                                    FirebaseDatabase.getInstance().reference
                                        .child("kullanici")
                                        .orderByKey()
                                        .equalTo(id)
                                        .addListenerForSingleValueEvent(object : ValueEventListener{

                                            override fun onCancelled(p0: DatabaseError) {
                                                TODO("Not yet implemented")
                                            }

                                            override fun onDataChange(p0: DataSnapshot) {
                                                var singleSnapshot = p0.children.iterator().next()

                                                var tokenOfUser = singleSnapshot.getValue(Kullanici::class.java)?.mesaj_token

                                                //simdi de retrofitle bildirimi gondermem gerekiyor modelim FCMModel
                                                //icinde de Data diye bir sınıfım var bilgilerimi buraya, to kısmına da bildirimi kime
                                                //atacagımı yazıcam

                                                var to = tokenOfUser

                                                var data = FCMModel.Data()
                                                data.baslik = "Yeni Mesajiniz Var"
                                                data.bildirimTuru = "Sohbet"
                                                data.icerik = mesaj
                                                data.sohbetOdasiId = sohbetOdasiID

                                                var bildirim = FCMModel()
                                                bildirim.data = data
                                                bildirim.to = to


                                                //istegimi hazirliyorum fonksiyonumun ilki parametreler icin 2. si ise atacagim model
                                                var request = myInterface.bildirimGonder(headers,bildirim)

                                                //bildirimi atiyorum
                                                request.enqueue(object : Callback<Response<FCMModel>>{
                                                    override fun onFailure(call: Call<Response<FCMModel>>, t: Throwable) {
                                                        TODO("Not yet implemented")
                                                    }

                                                    override fun onResponse(call: Call<Response<FCMModel>>, response: Response<Response<FCMModel>>) {
                                                        //POST ISTEKTE BULUNDUGUM ICIN BIR CEVAP BEKLEMIYORUM
                                                        println("bildirim gonderildi retrofit")

                                                    }

                                                })


                                            }

                                        })


                                }


                            }

                        }

                    })





            }


        }

        etMesaj.setOnClickListener {

            rvMesajlar.smoothScrollToPosition(myAdapter!!.itemCount-1)

        }



    }

    fun serverKeyiAl(){

        var ref = FirebaseDatabase.getInstance().reference
        ref.child("server")
            .addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var singleSnapshot = p0.children.iterator().next()
                    SERVER_KEY = singleSnapshot.getValue().toString()
                }

            })

    }

    //databeste bir degisiklik olursa burasi tetikleniyor
    var myValueEventListener = object : ValueEventListener{

        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {

            mesajlariGetir()

        }

    }

    //databese evet listeneri ekliyorum
    fun baslatMesajListener(){

        myReference = FirebaseDatabase.getInstance().reference
            .child("sohbet_odasi")
            .child(sohbetOdasiID.toString())
            .child("sohbet_odasi_mesajlari")

        myReference.addValueEventListener(myValueEventListener)

    }

    //mesajlari getiriyor
    fun mesajlariGetir(){

        if(tumMesajlar == null){
            tumMesajlar = ArrayList()
            myHashSet = HashSet()
        }

        if(myAdapter == null){
            initMesajlarListesi()
        }

        myReference.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                for(sohbetMesaj in p0.children){

                    var tmpSohbetMesaj = sohbetMesaj.getValue(SohbetMesaj::class.java)

                    if(!myHashSet!!.contains(sohbetMesaj.key)){
                        myHashSet!!.add(sohbetMesaj.key.toString())
                        tumMesajlar!!.add(tmpSohbetMesaj!!)
                        myAdapter!!.notifyDataSetChanged()
                    }
                }

                rvMesajlar.scrollToPosition(myAdapter!!.itemCount-1)

            }

        })





    }

    fun initMesajlarListesi(){
        myAdapter = MesajlarAdapter(this,tumMesajlar!!)
        rvMesajlar.adapter = myAdapter
        rvMesajlar.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rvMesajlar.scrollToPosition(myAdapter!!.itemCount -1)


    }






    //atilan mesajin tarihi icin
    fun getTarih():String{
        var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("tr"))
        return sdf.format(Date())
    }






    //Kullanıcı Kontrolü kısmı
    //------------------------------------------------------------------------------------------------------------
    fun baslatFirebaseAuthStateListener(){
        myAuthStateListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = p0.currentUser
                if(user == null){
                    var intent = Intent(this@MesajlarActivity,GirisEkrani::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        activityAcikMi = true
        FirebaseAuth.getInstance().addAuthStateListener(myAuthStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        activityAcikMi = false
        FirebaseAuth.getInstance().removeAuthStateListener(myAuthStateListener!!)
    }

    override fun onResume() {
        super.onResume()
        kullaniciKontrolEt()
    }

    fun kullaniciKontrolEt(){
        var user = FirebaseAuth.getInstance().currentUser
        if(user == null){
            var intent = Intent(this,GirisEkrani::class.java)
            startActivity(intent)
            finish()
        }
    }
    //------------------------------------------------------------------------------------------------------------



}