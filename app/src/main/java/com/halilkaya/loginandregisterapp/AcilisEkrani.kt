package com.halilkaya.loginandregisterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.halilkaya.loginandregisterapp.Fragments.MyDialogFragment
import kotlinx.android.synthetic.main.activity_acilis_ekrani.*

class AcilisEkrani : AppCompatActivity() {

    lateinit var mAuthStateListener:FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acilis_ekrani)
        initAuthStateListener()



        btnGirisYap.setOnClickListener {

            var intent = Intent(this,GirisEkrani::class.java)
            startActivity(intent)

        }

        btnKaydol.setOnClickListener {

            var intent = Intent(this,KayitEkrani::class.java)
            startActivity(intent)

        }


    }



    fun initAuthStateListener(){


        mAuthStateListener = object : FirebaseAuth.AuthStateListener{

            override fun onAuthStateChanged(p0: FirebaseAuth) {

                var kullanici = p0.currentUser
                if(kullanici != null){


                    var intent = Intent(this@AcilisEkrani, MainActivity::class.java)
                    startActivity(intent)
                    finish()


                }

            }


        }

    }


    override fun onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener)
        super.onStart()
    }

    override fun onDestroy() {
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener)
        super.onDestroy()
    }



}
