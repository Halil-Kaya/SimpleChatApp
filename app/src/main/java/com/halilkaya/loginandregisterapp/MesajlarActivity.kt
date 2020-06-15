package com.halilkaya.loginandregisterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.halilkaya.loginandregisterapp.adapter.MesajlarAdapter
import com.halilkaya.loginandregisterapp.model.Kullanici
import com.halilkaya.loginandregisterapp.model.SohbetMesaj
import kotlinx.android.synthetic.main.activity_mesajlar.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class MesajlarActivity : AppCompatActivity() {

    var myAuthStateListener:FirebaseAuth.AuthStateListener? = null
    var sohbetOdasiID:String? = ""

    var myAdapter : MesajlarAdapter? = null

    var myHashSet:HashSet<String>? = null


    //databesimde mesajlar kismini referans alicak boylece o kisimda bir degisiklik oldugunda
    //yani yeni mesaj geldiginde burasi tetiklenecek
    lateinit var myReference:DatabaseReference

    var tumMesajlar:ArrayList<SohbetMesaj>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesajlar)
        baslatFirebaseAuthStateListener()
        init()
        baslatMesajListener()


    }
    fun init(){

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

                etMesaj.setText("")

            }


        }

        etMesaj.setOnClickListener {

            rvMesajlar.smoothScrollToPosition(myAdapter!!.itemCount-1)

        }



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
        FirebaseAuth.getInstance().addAuthStateListener(myAuthStateListener!!)
    }

    override fun onStop() {
        super.onStop()
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