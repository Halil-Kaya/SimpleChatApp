package com.halilkaya.loginandregisterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.halilkaya.loginandregisterapp.Fragments.MyDialogFragment
import kotlinx.android.synthetic.main.activity_sifre_alma_ekrani.*

class SifreAlmaEkrani : AppCompatActivity(),MyListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sifre_alma_ekrani)

        btnGonder.setOnClickListener {

            var myDialogFragment = MyDialogFragment()

            if(etMail.text.isNotEmpty()){

                //mail gonderecek


            }else{

                myDialogFragment.setAciklama("bir mail giriniz")
                myDialogFragment.show(supportFragmentManager,"frg")

            }





        }


    }

    override fun kapat() {
        finish()
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
            var intent = Intent(activity,AcilisEkrani::class.java)
            startActivity(intent)
            mListener.kapat()


        }


        return view
    }



}








