package com.halilkaya.loginandregisterapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.halilkaya.loginandregisterapp.fragments.MyDialogFragment
import com.halilkaya.loginandregisterapp.model.Kullanici
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_hesap_ayarlari.*
import java.io.ByteArrayOutputStream

class HesapAyarlari : AppCompatActivity(),MyListenerHesapAyarlari,onProfilResmiListener {

    lateinit var kullanici:FirebaseUser
    var myDialogFragment = MyDialogFragment()

    var izinlerVerildiMi:Boolean = false


    var galeridenGelen:Uri? = null
    var kameradanGelen:Bitmap? = null

    override fun getResimYolu(resimPath: Uri?) {

        galeridenGelen = resimPath
        Picasso.get().load(galeridenGelen).resize(96,96).into(imgProfilResmi)


    }

    override fun getResimBitmap(bitmap: Bitmap) {

        kameradanGelen = bitmap
        imgProfilResmi.setImageBitmap(kameradanGelen)

    }

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


                                if(etTelefonNumarasi.text.isNotEmpty()){
                                    updateTelefonNumarasi()
                                }


                            }else{
                                progresbarGizle()
                                myDialogFragment.setAciklama("sifreniz yanlis: ${p0.exception?.message}")
                                myDialogFragment.show(supportFragmentManager,"sifreYanlis")


                            }

                        }

                    })





                }else{

                    progresbarGizle()
                    myDialogFragment.setAciklama("kaydetmek icin sifrenizi giriniz")
                    myDialogFragment.show(supportFragmentManager,"frag")

                }

            //resmi kaydedip storage eklemeden önce resmin boyutunu küçültmem lazım
            if(galeridenGelen != null){

                progresbarGoster()

                fotografCompressed(galeridenGelen)

            }else if(kameradanGelen != null){

                progresbarGoster()

                fotografCompressed(kameradanGelen)



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



        imgProfilResmi.setOnClickListener {


            if(izinlerVerildiMi){

                var profilResmiDialog = MyDialogFragmentProfilResmi()
                profilResmiDialog.show(supportFragmentManager,"frag-ok")

            }else{
                izinleriIste()
            }


        }




    }

    //galeriden secilen resmin boyutunu küçültüyorum ve bunu arka planda yapıyorum
    fun fotografCompressed(galeridenGelen:Uri?){

        var compressed = BackgroundResimCompress()
        compressed.execute(galeridenGelen)

    }

    //kameradan çekilen resmin boyutunu küçültüyorum ve bunu arka planda yapıyorum
    fun fotografCompressed(kameradanGelen:Bitmap?){

        var compresed = BackgroundResimCompress()
        var uri:Uri? = null
        compresed.execute(uri)

    }




    inner class BackgroundResimCompress : AsyncTask<Uri,Void,ByteArray>{

        var myBitmap:Bitmap? = null

        constructor(){}

        constructor(bitmap: Bitmap){
            myBitmap = bitmap
        }

        //işlem başlamadan önce girilen yer
        override fun onPreExecute() {
            super.onPreExecute()
        }

        //arka planda işlem yapılan yer
        override fun doInBackground(vararg params: Uri?): ByteArray {

            if(myBitmap == null){
                myBitmap = MediaStore.Images.Media.getBitmap(this@HesapAyarlari.contentResolver,params[0])
            }

            var resimBytes:ByteArray? = null

            for(i in 1..5){
                resimBytes = convertBitmaptoByte(myBitmap,100/i)
            }

            return resimBytes!!

        }

        //resmin boyutunu küçültüğüm yer
        fun convertBitmaptoByte(myBitmap:Bitmap?,i:Int):ByteArray?{

            var stream = ByteArrayOutputStream()
            myBitmap?.compress(Bitmap.CompressFormat.JPEG,i,stream)
            return stream.toByteArray()

        }



        //main threadle haberleşmesini sağlayan bir yer
        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        //sonucun geldiği yer
        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)

            uploadResimToFirebase(result)


        }



    }


    //boyutu küçültülmüş resmi firebase yüklüyorum
    fun uploadResimToFirebase(result:ByteArray?){

        var storageReferans = FirebaseStorage.getInstance().reference

        //resim nereye eklensin (storage kısmı için)
        var resimEklenecekYer = storageReferans.child("images/users/"+kullanici.uid+"/profil_resmi")


        var uploadGorevi = resimEklenecekYer.putBytes(result!!)

        uploadGorevi.addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot>{

            override fun onSuccess(p0: UploadTask.TaskSnapshot?) {

                resimEklenecekYer.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri>{


                    override fun onSuccess(p0: Uri?) {

                        var profilResmiUrl = p0.toString()


                        FirebaseDatabase.getInstance().reference
                            .child("kullanici")
                            .child(kullanici.uid)
                            .child("profil_resmi")
                            .setValue(profilResmiUrl)

                        progresbarGizle()


                    }

                })

            }

        })



    }



    //gerekli izinleri istiyorum
    fun izinleriIste(){

        var izinler = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA)

        if(ContextCompat.checkSelfPermission(this,izinler[0]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this,izinler[1]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this,izinler[2]) == PackageManager.PERMISSION_GRANTED){

            izinlerVerildiMi = true
        }else{
            ActivityCompat.requestPermissions(this,izinler,150)

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {


        if(requestCode == 150){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED){

                var profilResmiFragment = MyDialogFragmentProfilResmi()
                profilResmiFragment.show(supportFragmentManager, "fotoSec")
            }
        }



        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }



    fun onaylamaMailiGonder(){

        kullanici.sendEmailVerification()


    }

    fun updateTelefonNumarasi(){

        FirebaseDatabase.getInstance().reference
            .child("kullanici")
            .child(kullanici.uid)
            .child("telefon")
            .setValue(etTelefonNumarasi.text.toString())
            .addOnCompleteListener(object : OnCompleteListener<Void>{

                override fun onComplete(p0: Task<Void>) {
                    if(p0.isSuccessful){

                    }else{

                        myDialogFragment.setAciklama("telefonunuzu guncellenirken bir hata oldu")
                        myDialogFragment.show(supportFragmentManager,"frag-TelErrorDatabes")
                    }
                }

            })


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


                        FirebaseDatabase.getInstance().reference
                            .child("kullanici")
                            .child(kullanici.uid)
                            .child("isim")
                            .setValue(etKullaniciAdi.text.toString())
                            .addOnCompleteListener(object : OnCompleteListener<Void>{

                                override fun onComplete(p0: Task<Void>) {

                                    if(p0.isSuccessful){

                                    }else{
                                        myDialogFragment.setAciklama("kullanici adiniz guncellenirken bir hata oldu")
                                        myDialogFragment.show(supportFragmentManager,"frag-ErrorDatabes")
                                    }


                                }

                            })

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
            progresbarGoster()
            etKullaniciAdi.setText(kullanici.displayName)
            etMail.setText(kullanici.email)

            var reference = FirebaseDatabase.getInstance().reference

            var sorgu = reference
                .child("kullanici")
                .orderByKey()
                .equalTo(kullanici.uid)

            sorgu.addListenerForSingleValueEvent(object : ValueEventListener{


                override fun onCancelled(p0: DatabaseError) {


                    progresbarGizle()
                }

                override fun onDataChange(p0: DataSnapshot) {

                    for(snapshot in p0.children){
                        var okunanKullanici = snapshot.getValue(Kullanici::class.java)

                        etTelefonNumarasi.setText(okunanKullanici?.telefon)

                        var profilResmiUrl = okunanKullanici?.profil_resmi+""

                        if(profilResmiUrl.isNotBlank() && profilResmiUrl.isNotEmpty()) {
                            Picasso.get().load(okunanKullanici?.profil_resmi).resize(96, 96)
                                .into(imgProfilResmi)
                        }

                    }
                    progresbarGizle()

                }


            })



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


interface onProfilResmiListener{

    fun getResimYolu(resimPath:Uri?)
    fun getResimBitmap(bitmap: Bitmap)

}

class MyDialogFragmentProfilResmi() : DialogFragment(){

    lateinit var tvKameradan:TextView
    lateinit var tvGaleriden:TextView

    lateinit var mProfilResmiListener:onProfilResmiListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.dialog_fragment_profilresmi,container,false)

        tvGaleriden = view.findViewById(R.id.tvGaleridenSec)
        tvGaleriden.setOnClickListener {

            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent,100)


        }


        tvKameradan = view.findViewById(R.id.tvKameradanSec)
        tvKameradan.setOnClickListener {

            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,200)


        }


        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //galeriden resim secildi
        if(requestCode == 100 && resultCode == Activity.RESULT_OK && data != null){

            var galeridenSecilenResimYolu = data.data
            mProfilResmiListener.getResimYolu(galeridenSecilenResimYolu)
            dismiss()

        }//kameradan resim çekildi
        else if(requestCode == 200 && resultCode == Activity.RESULT_OK && data != null){

            var kameradanCekilenResim = data.extras?.get("data") as Bitmap

            mProfilResmiListener.getResimBitmap(kameradanCekilenResim)
            dismiss()

        }

    }


    override fun onAttach(context: Context) {
        mProfilResmiListener = activity as onProfilResmiListener
        super.onAttach(context)
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







