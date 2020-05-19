package com.halilkaya.loginandregisterapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.halilkaya.loginandregisterapp.R
import com.halilkaya.loginandregisterapp.acilisEkrani
import kotlinx.android.synthetic.main.dialog_fragment_design.*

class MyDialogFragment() : DialogFragment() {

    var dialogAciklama:String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.dialog_fragment_design,container,false)



        var tvAciklama = view.findViewById<TextView>(R.id.tvAciklama)
        var tvKapatmaButtonu = view.findViewById<TextView>(R.id.tvKapatmaButonu)

        tvAciklama.setText(dialogAciklama)

        tvKapatmaButtonu.setOnClickListener {
            dismiss()
        }

        return view

    }

    fun setAciklama(dialogAciklama:String){
        this.dialogAciklama = dialogAciklama

    }



}