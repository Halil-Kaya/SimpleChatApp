package com.halilkaya.loginandregisterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.halilkaya.loginandregisterapp.Fragments.MyDialogFragment
import kotlinx.android.synthetic.main.activity_hesap_ayarlari.*
import kotlinx.android.synthetic.main.dialog_fragment_design.*
import java.security.AuthProvider

class HesapAyarlari : AppCompatActivity(),MyListenerHesapAyarlari {

    lateinit var kullanici:FirebaseUser
    var myDialogFragment = MyDialogFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hesap_ayarlari)

        kullanici = FirebaseAuth.getInstance().currentUser!!
        setKullaniciBilgileri()



        tvMailSifreGuncelle.setOnClickListener {

            if(etSifre.text.isNotEmpty()){

                var credential = EmailAuthProvider.getCredential(etMail.text.toString(),etSifre.text.toString())

                kullanici.reauthenticate(credential)
                    .addOnCompleteListener(object : OnCompleteListener<Void>{

                        override fun onComplete(p0: Task<Void>) {

                            if(p0.isSuccessful){

                                constrainLayoutuGoster()

                            }else{

                                myDialogFragment.setAciklama("sifre yanlis")
                                myDialogFragment.show(supportFragmentManager,"sifreYanlis")

                            }

                        }

                    })



            }else{
                myDialogFragment.setAciklama("sifrenizi giriniz")
                myDialogFragment.show(supportFragmentManager,"sifreGirilmedi")
            }



        }


        btnKaydet.setOnClickListener {

            progresbarGoster()
            if(etSifre.text.isNotEmpty()){

                var credential = EmailAuthProvider.getCredential(kullanici.email.toString(),etSifre.text.toString())
                kullanici.reauthenticate(credential)
                    .addOnCompleteListener(object : OnCompleteListener<Void>{

                        override fun onComplete(p0: Task<Void>) {

                            if(p0.isSuccessful){


                                //burdaki kontrol yapısı düzeltilecek

                                if(!etKullaniciAdi.text.equals(kullanici.displayName)){

                                    updateKullaniciAdi()

                                }

                                /*
                                if(!etTelefonNumarasi.text.equals(/*telefon sorgusu*/)){
                                    //telefonu guncelliyecem
                                }*/


                            }else{
                                progresbarGizle()
                                myDialogFragment.setAciklama("sifreniz yanlis")
                                myDialogFragment.show(supportFragmentManager,"sifreYanlis")


                            }

                        }

                    })





                }else{

                    progresbarGizle()
                    myDialogFragment.setAciklama("kaydetmek icin sifrenizi giriniz")
                    myDialogFragment.show(supportFragmentManager,"frag")

                }



        }


        tvSifremiUnuttum.setOnClickListener {

            progresbarGoster()


            if(etMail.text.isNotEmpty()){
                FirebaseAuth.getInstance().sendPasswordResetEmail(etMail.text.toString())
                    .addOnCompleteListener(object : OnCompleteListener<Void>{

                        override fun onComplete(p0: Task<Void>) {

                            progresbarGizle()
                            if(p0.isSuccessful){

                                myDialogFragment.setAciklama("sifreniz resetlenti mailinizi kontrol edin")
                                myDialogFragment.show(supportFragmentManager,"resetlendi")

                            }else{

                                myDialogFragment.setAciklama("bir hata olustu")
                                myDialogFragment.show(supportFragmentManager,"hataoldu")

                            }

                        }

                    })
            }


        }


        btnMailGuncelle.setOnClickListener {
            progresbarGoster()
            if(etYeniMail.text.isNotEmpty()){

                kullanici.updateEmail(etYeniMail.text.toString())
                    .addOnCompleteListener(object : OnCompleteListener<Void>{

                        override fun onComplete(p0: Task<Void>) {

                            progresbarGizle()
                            if(p0.isSuccessful){

                                onaylamaMailiGonder()
                                var myDialogFragmentHesapAyarlari = MyDialogFragmentHesapAyarlari()
                                myDialogFragmentHesapAyarlari.show(supportFragmentManager,"frag")


                            }else{
                                myDialogFragment.setAciklama(p0.exception?.message+"")
                                myDialogFragment.show(supportFragmentManager,"updateMailError")
                            }

                        }


                    })


            }else{
                myDialogFragment.setAciklama("bir mail giriniz")
                myDialogFragment.show(supportFragmentManager,"mailYok")
            }


        }

        btnSifreGuncelle.setOnClickListener {

            progresbarGoster()
            if(etYeniSifre.text.isNotEmpty()){

                kullanici.updatePassword(etYeniSifre.text.toString())
                    .addOnCompleteListener(object : OnCompleteListener<Void>{


                        override fun onComplete(p0: Task<Void>) {
                            progresbarGizle()
                            if(p0.isSuccessful){


                                myDialogFragment.setAciklama("sifreniz Guncellendi")
                                myDialogFragment.show(supportFragmentManager,"updatePassword")

                                setKullaniciBilgileri()

                            }else{


                                myDialogFragment.setAciklama(p0.exception?.message+"")
                                myDialogFragment.show(supportFragmentManager,"updatePasswordError")

                            }

                        }


                    })


            }else{

                myDialogFragment.setAciklama("bir sifre giriniz")
                myDialogFragment.show(supportFragmentManager,"sifreYok")

            }

        }


    }



    fun onaylamaMailiGonder(){

        kullanici.sendEmailVerification()


    }

    fun updateKullaniciAdi(){

        var bilgileriGuncelle = UserProfileChangeRequest.Builder()
            .setDisplayName(etKullaniciAdi.text.toString())
            .build()
        kullanici.updateProfile(bilgileriGuncelle)
            .addOnCompleteListener(object : OnCompleteListener<Void>{

                override fun onComplete(p0: Task<Void>) {
                    progresbarGizle()
                    if(p0.isSuccessful){
                        myDialogFragment.setAciklama("kullanici adiniz guncellendi")
                        myDialogFragment.show(supportFragmentManager,"frag")
                        setKullaniciBilgileri()
                    }else{
                        myDialogFragment.setAciklama("kullanici adiniz guncellenirken bir hata oldu")
                        myDialogFragment.show(supportFragmentManager,"frag")
                    }
                }

            })
    }

    fun setKullaniciBilgileri(){

        if(kullanici == null){
            cikisYap()
        }else{

            etKullaniciAdi.setText(kullanici.displayName)
            etTelefonNumarasi.setText("simdilik yok")
            etMail.setText(kullanici.email)


        }


    }

    override fun cikisYap(){
        FirebaseAuth.getInstance().signOut()
    }

    fun progresbarGoster(){
        pbHesapAyarlari.visibility = View.VISIBLE
    }

    fun progresbarGizle(){
        pbHesapAyarlari.visibility = View.INVISIBLE
    }

    fun constrainLayoutuGizle(){
        gizliConstrainLayout.visibility = View.INVISIBLE
    }

    fun constrainLayoutuGoster(){
        gizliConstrainLayout.visibility = View.VISIBLE
    }

}







interface MyListenerHesapAyarlari{
    fun cikisYap()
}




class MyDialogFragmentHesapAyarlari() : DialogFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.dialog_fragment_design,container,false)


        var tvAciklama = view.findViewById<TextView>(R.id.tvAciklama)
        tvAciklama.setText("mail guncellendi mailinizi onaylayın ve tekrar giris yapin")

        var tvKapatmaButonu = view.findViewById<TextView>(R.id.tvKapatmaButonu)
        tvKapatmaButonu.setOnClickListener {

            var myListener = activity as MyListenerHesapAyarlari
            dismiss()
            myListener.cikisYap()
        }


        return view
    }

}







