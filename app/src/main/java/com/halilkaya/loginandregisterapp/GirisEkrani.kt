package com.halilkaya.loginandregisterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.halilkaya.loginandregisterapp.Fragments.MyDialogFragment
import kotlinx.android.synthetic.main.activity_giris_ekrani.*
import kotlinx.android.synthetic.main.dialog_fragment_kayit_ekrani.*


class GirisEkrani : AppCompatActivity(),MyListenerGirisEkrani {

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

                            var myDialogFragmentGirisEkrani = MyDialogFragmentGirisEkrani()
                            myDialogFragmentGirisEkrani.show(supportFragmentManager,"frag")

                        }



                    }else{

                        myDialogFragment.setAciklama(p0.exception?.message+"")
                        myDialogFragment.show(supportFragmentManager,"frag")

                        etSifre.setText("")

                    }

                }


            })




    }


    override fun mailiTekrarGonder() {

        progresbarGoster()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(etMail.text.toString(),etSifre.text.toString())
            .addOnCompleteListener(object : OnCompleteListener<AuthResult>{

                override fun onComplete(p0: Task<AuthResult>) {
                    if(p0.isSuccessful){

                        mailGonder(p0.result?.user)

                    }else{
                        progresbarGizle()
                        Toast.makeText(this@GirisEkrani,"bir hata oldu",Toast.LENGTH_SHORT).show()
                    }
                }

            })

    }

    fun mailGonder(kullanici:FirebaseUser?){

        var myDialogFragment = MyDialogFragment()

        if(kullanici != null){

            kullanici.sendEmailVerification()
                .addOnCompleteListener(object : OnCompleteListener<Void>{

                    override fun onComplete(p0: Task<Void>) {

                        progresbarGizle()
                        if(p0.isSuccessful){
                            FirebaseAuth.getInstance().signOut()
                            Toast.makeText(this@GirisEkrani,"mailinizi kontrol edin",Toast.LENGTH_SHORT).show()

                        }else{

                            myDialogFragment.setAciklama(p0.exception?.message+"")
                            myDialogFragment.show(supportFragmentManager,"frag")

                        }

                    }

                })


        }
    }


    fun progresbarGizle(){
        pbGirisEkrani.visibility = View.INVISIBLE
    }
    fun progresbarGoster(){
        pbGirisEkrani.visibility = View.VISIBLE
    }



}


interface MyListenerGirisEkrani{
    fun mailiTekrarGonder()
}



class MyDialogFragmentGirisEkrani() : DialogFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.dialog_fragment_giris_ekrani_mail,container,false)

        var myListenerGirisEkrani = activity as MyListenerGirisEkrani

        var tvKapatmaButonu = view.findViewById<TextView>(R.id.tvKapatmaButonu)
        tvKapatmaButonu.setOnClickListener {
            dismiss()
        }

        var tvOnaylamaMailiniTekrarGonder = view.findViewById<TextView>(R.id.tvOnaylamaMailiniTekrarGonder)
        tvOnaylamaMailiniTekrarGonder.setOnClickListener {

            myListenerGirisEkrani.mailiTekrarGonder()
            dismiss()

        }


        return view
    }


}




















