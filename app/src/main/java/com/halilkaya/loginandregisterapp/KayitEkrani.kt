package com.halilkaya.loginandregisterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.halilkaya.loginandregisterapp.Fragments.MyDialogFragment
import kotlinx.android.synthetic.main.activity_kayit_ekrani.*

class KayitEkrani : AppCompatActivity(),MyListenerKayitEkrani {

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


                    kayıtYap(etMail.text.toString(),etSifre.text.toString())


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

    fun kayıtYap(mail:String,sifre:String){
        progresbarGoster()
        var myDialogFragment = MyDialogFragment()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail,sifre)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult>{

                override fun onComplete(p0: Task<AuthResult>) {

                    progresbarGizle()
                    if(p0.isSuccessful){

                        onaylamaMailiGonder(p0.result?.user)

                    }else{

                        myDialogFragment.setAciklama(p0.exception?.message+"")
                        myDialogFragment.show(supportFragmentManager,"frag")

                    }


                }


            })


    }

    fun onaylamaMailiGonder(kullanici:FirebaseUser?){
        var myDialogFragment = MyDialogFragment()

        if(kullanici != null){

            kullanici.sendEmailVerification()
                .addOnCompleteListener(object : OnCompleteListener<Void>{

                    override fun onComplete(p0: Task<Void>) {

                        if(p0.isSuccessful){
                            FirebaseAuth.getInstance().signOut()
                            var myDialogFragmentKayitEkrani = MyDialogFragmentKayitEkrani()
                            myDialogFragmentKayitEkrani.show(supportFragmentManager,"frag")

                        }else{

                            myDialogFragment.setAciklama(p0.exception?.message+"")
                            myDialogFragment.show(supportFragmentManager,"frag")

                        }

                    }

                })


        }

    }



    fun progresbarGoster(){

        pbKayitEkrani.visibility = View.VISIBLE

    }

    fun progresbarGizle(){

        pbKayitEkrani.visibility = View.INVISIBLE

    }

    override fun kapat() {
        var intent = Intent(this@KayitEkrani,GirisEkrani::class.java)
        startActivity(intent)
        finish()
    }


}


interface MyListenerKayitEkrani{

    fun kapat()

}


class MyDialogFragmentKayitEkrani: DialogFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.dialog_fragment_kayit_ekrani,container,false)
        var tvKapatmaButonu = view.findViewById<TextView>(R.id.tvKapatmaButonu)

        var myListener = activity as MyListenerKayitEkrani

        tvKapatmaButonu.setOnClickListener {
            dismiss()
            myListener.kapat()

        }


        return view
    }


}












