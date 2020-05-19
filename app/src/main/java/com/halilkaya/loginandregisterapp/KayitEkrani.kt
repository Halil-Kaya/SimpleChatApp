package com.halilkaya.loginandregisterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.halilkaya.loginandregisterapp.Fragments.MyDialogFragment
import kotlinx.android.synthetic.main.activity_kayit_ekrani.*

class KayitEkrani : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kayit_ekrani)

        tvHesapVar.setOnClickListener {

            var intent = Intent(this,GirisEkrani::class.java)
            startActivity(intent)
            finish()

        }



        btnKaydol.setOnClickListener {

            var myDialogFragment = MyDialogFragment()

            if(etMail.text.isNotEmpty() && etSifre.text.isNotEmpty() && etSifreTekrar.text.isNotEmpty()){

                if(etSifre.text.toString().equals(etSifreTekrar.text.toString())){


                    //kayit yapabilir

                }else{

                    myDialogFragment.setAciklama("sifreler uyusmuyor")
                    myDialogFragment.show(supportFragmentManager,"frg")

                }




            }else{

            myDialogFragment.setAciklama("bilgileri giriniz")
            myDialogFragment.show(supportFragmentManager,"frg")

            }




        }

    }
}
