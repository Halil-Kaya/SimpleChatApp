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
import com.facebook.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.halilkaya.loginandregisterapp.Fragments.MyDialogFragment
import kotlinx.android.synthetic.main.activity_giris_ekrani.*
import kotlinx.android.synthetic.main.dialog_fragment_kayit_ekrani.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider


class GirisEkrani : AppCompatActivity(),MyListenerGirisEkrani {

    var callbackManager:CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_giris_ekrani)
        FacebookSdk.sdkInitialize(applicationContext)

        callbackManager = CallbackManager.Factory.create()



        
        login_button.registerCallback(callbackManager,object : FacebookCallback<LoginResult>{


            override fun onSuccess(result: LoginResult?) {
                progresbarGoster()
                handleFacebookAccessToken(result?.accessToken)


            }

            override fun onCancel() {
                println("onCancel")
            }

            override fun onError(error: FacebookException?) {
                println("onError")
            }

        })


        tvSifreUnuttum.setOnClickListener {

            var intent = Intent(this,SifreAlmaEkrani::class.java)
            startActivity(intent)

        }

        tvHesapVar.setOnClickListener {

            var intent = Intent(this,KayitEkrani::class.java)
            startActivity(intent)
            finish()

        }


        btnGirisYap.setOnClickListener {

            var myDialogFragment = MyDialogFragment()

            if(etMail.text.isNotEmpty() && etSifre.text.isNotEmpty()){


                girisYap(etMail.text.toString(),etSifre.text.toString())



            }else{

                myDialogFragment.setAciklama("bilgileri giriniz")
                myDialogFragment.show(supportFragmentManager,"frag-BilgileriGiriniz")


            }

        }

     


    }


    fun handleFacebookAccessToken(result:AccessToken?){

        var credential = FacebookAuthProvider.getCredential(result?.token!!)

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult>{


                override fun onComplete(p0: Task<AuthResult>) {
                    progresbarGizle()
                    if(p0.isSuccessful){
                        var intent = Intent(this@GirisEkrani, MainActivity::class.java)
                        startActivity(intent)
                        finish()


                    }else{

                    }

                }

            })



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
                            myDialogFragmentGirisEkrani.show(supportFragmentManager,"frag-mailIsNotVerified")

                        }



                    }else{

                        myDialogFragment.setAciklama(p0.exception?.message+"")
                        myDialogFragment.show(supportFragmentManager,"frag-Error")

                        etSifre.setText("")

                    }

                }


            })




    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager?.onActivityResult(requestCode,resultCode,data)

        super.onActivityResult(requestCode, resultCode, data)
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




















