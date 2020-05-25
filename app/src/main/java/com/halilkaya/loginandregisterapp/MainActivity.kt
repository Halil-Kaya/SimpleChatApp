package com.halilkaya.loginandregisterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var myAuthStateListener:FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAuthStateListener()



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
