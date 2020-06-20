package com.halilkaya.loginandregisterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.ktx.Firebase
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var myAuthStateListener:FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAuthStateListener()
        init()
        initFCM()

        //bildirim kismindan tiklarsa onu mesajlar kismina aticam
        getPendingIntent()


    }



    fun init(){

        btnCikisYap.setOnClickListener {
            cikisYap()
            var intent = Intent(this,AcilisEkrani::class.java)
            startActivity(intent)
            finish()
        }
        btnHesapAyarlari.setOnClickListener {
            var intent = Intent(this,HesapAyarlari::class.java)
            startActivity(intent)
        }

        btnSohbetOdasi.setOnClickListener {
            var intent = Intent(this,SohbetOdalariActivity::class.java)
            startActivity(intent)
        }

    }

    fun getPendingIntent(){

        var gelenIntent = intent

        if(gelenIntent.hasExtra("sohbet_odasi_id")){
            var intent = Intent(this,MesajlarActivity::class.java)
            intent.putExtra("sohbetID",gelenIntent.getStringExtra("sohbet_odasi_id"))
            startActivity(intent)
        }


    }

    override fun onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(myAuthStateListener)
        super.onStart()
    }

    override fun onDestroy() {
        FirebaseAuth.getInstance().removeAuthStateListener(myAuthStateListener)
        super.onDestroy()
    }

    override fun onResume() {
        kullaniciKontrolEt()
        super.onResume()
    }


    fun initAuthStateListener(){
        myAuthStateListener = object : FirebaseAuth.AuthStateListener{

            override fun onAuthStateChanged(p0: FirebaseAuth) {

                var kullanici = p0.currentUser

                if(kullanici == null){
                    var intent = Intent(this@MainActivity,AcilisEkrani::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }


    fun initFCM(){
        var ref = FirebaseDatabase.getInstance().reference
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(object :
            OnCompleteListener<InstanceIdResult> {
            override fun onComplete(p0: Task<InstanceIdResult>) {
                var newToken = p0.result?.token
                ref.child("kullanici")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .child("mesaj_token")
                    .setValue(newToken)
            }
        })



    }


    fun kullaniciKontrolEt(){
        var kullanici = FirebaseAuth.getInstance().currentUser


        if(kullanici == null){
            cikisYap()
        }

    }

    fun cikisYap(){
        FirebaseAuth.getInstance().signOut()
        var intent = Intent(this,AcilisEkrani::class.java)
        startActivity(intent)
    }





}
