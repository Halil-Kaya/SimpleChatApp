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
import com.google.firebase.auth.FirebaseAuth
import com.halilkaya.loginandregisterapp.fragments.MyDialogFragment
import kotlinx.android.synthetic.main.activity_sifre_alma_ekrani.*

class SifreAlmaEkrani : AppCompatActivity(),MyListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sifre_alma_ekrani)

        btnGonder.setOnClickListener {

            var myDialogFragment = MyDialogFragment()

            if(etMail.text.isNotEmpty()){

                sifreResetMail(etMail.text.toString())


            }else{

                myDialogFragment.setAciklama("bir mail giriniz")
                myDialogFragment.show(supportFragmentManager,"frg")

            }

        }

    }

    fun sifreResetMail(mail:String){

        progresbarGoster()

        FirebaseAuth.getInstance().sendPasswordResetEmail(mail)
            .addOnCompleteListener(object : OnCompleteListener<Void>{

                override fun onComplete(p0: Task<Void>) {

                    progresbarGizle()
                    if(p0.isSuccessful){

                        var myDialogFragmentForPassword = MyDialogFragmentForPassword()
                        myDialogFragmentForPassword.show(supportFragmentManager,"frag")

                    }else{

                        var myDialogFragment = MyDialogFragment()
                        myDialogFragment.setAciklama("bir hata oldu sonra tekrar deneyiniz")
                        myDialogFragment.show(supportFragmentManager,"frag")

                    }

                }

            })



    }




    override fun kapat() {
        finish()
    }


    fun progresbarGoster(){
        pbSifreAlmaEkrani.visibility = View.VISIBLE
    }

    fun progresbarGizle(){
        pbSifreAlmaEkrani.visibility = View.INVISIBLE
    }


}

interface MyListener{
    fun kapat()
}


class MyDialogFragmentForPassword() : DialogFragment(){


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.dialog_fragment_sifre_reset,container,false)

        var tvKapatmaBtn = view.findViewById<TextView>(R.id.tvKapatmaButonu)

        var mListener = activity as MyListener

        tvKapatmaBtn.setOnClickListener {
            dismiss()
            var intent = Intent(activity,GirisEkrani::class.java)
            startActivity(intent)
            mListener.kapat()


        }


        return view
    }



}








