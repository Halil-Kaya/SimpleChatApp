package com.halilkaya.loginandregisterapp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FCMModel {
    @Expose
    @SerializedName("data")
    var data: Data? = null

    @Expose
    @SerializedName("to")
    var to: String? = null

    class Data {
        @Expose
        @SerializedName("sohbet_odasi_id")
        var sohbetOdasiId: String? = null

        @Expose
        @SerializedName("bildirim_turu")
        var bildirimTuru: String? = null

        @Expose
        @SerializedName("icerik")
        var icerik: String? = null

        @Expose
        @SerializedName("baslik")
        var baslik: String? = null

    }
}