package com.halilkaya.loginandregisterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.halilkaya.loginandregisterapp.Fragments.MyDialogFragment
import kotlinx.android.synthetic.main.activity_giris_ekrani.*


class GirisEkrani : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_giris_ekrani)

        tvSifreUnuttum.setOnClickListener {

            var intent = Intent(this,SifreAlmaEkrani::class.java)
            startActivity(intent)

        }



        btnGirisYap.setOnClickListener {

            var myDialogFragment = MyDialogFragment()

            if(etMail.text.isNotEmpty() && etSifre.text.isNotEmpty()){


                girisYap(etMail.text.toString(),etSifre.text.toString())



            }else{

                myDialogFragment.setAciklama("bilgileri giriniz")
                myDialogFragment.show(supportFragmentManager,"frag")


            }



        }

    }

    fun girisYap(mail:String,sifre:String){
        progresbarGoster()
        var myDialogFragment = MyDialogFragment()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(mail,sifre)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult>{

                override fun onComplete(p0: Task<AuthResult>) {
                    progresbarGizle()

                    if(p0.isSuccessful){

                        if(FirebaseAuth.getInstance().currentUser?.isEmailVerified == true){
                            var intent = Intent(this@GirisEkrani, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else{
                            myDialogFragment.setAciklama("mailinizi onaylayın")
                            myDialogFragment.show(supportFragmentManager,"frag")
                        }



                    }else{

                        myDialogFragment.setAciklama(p0.exception?.message+"")
                        myDialogFragment.show(supportFragmentManager,"frag")

                        etSifre.setText("")

                    }

                }


            })




    }

    fun progresbarGizle(){
        pbGirisEkrani.visibility = View.INVISIBLE
    }
    fun progresbarGoster(){
        pbGirisEkrani.visibility = View.VISIBLE
    }

}
