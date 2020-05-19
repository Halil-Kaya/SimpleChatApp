package com.halilkaya.loginandregisterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

                //login yapıcak


            }else{

                myDialogFragment.setAciklama("bilgileri giriniz")
                myDialogFragment.show(supportFragmentManager,"frag")


            }



        }

    }
}
