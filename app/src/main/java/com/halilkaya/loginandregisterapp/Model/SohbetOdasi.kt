package com.halilkaya.loginandregisterapp.Model

class SohbetOdasi {

    var sohbet_odasi_adi:String = ""
    var olusturan_id:String = ""
    var seviye:String = ""
    var sohbet_odasi_id = ""
    var sohbet_odasi_mesajlari:ArrayList<SohbetMesaj>? =null

    constructor(){
    }

    constructor(sohbet_odasi_adi:String,olusturan_id:String,seviye:String,sohbet_odasi_id:String,sohbet_odasi_mesajlari:ArrayList<SohbetMesaj>){
        this.sohbet_odasi_adi = sohbet_odasi_adi
        this.olusturan_id = olusturan_id
        this.seviye = seviye
        this.sohbet_odasi_id = sohbet_odasi_id
        this.sohbet_odasi_mesajlari = sohbet_odasi_mesajlari
    }

}