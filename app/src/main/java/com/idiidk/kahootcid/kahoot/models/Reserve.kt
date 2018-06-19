package com.idiidk.kahootcid.kahoot

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Reserve {

    @SerializedName("twoFactorAuth")
    @Expose
    var twoFactorAuth: Boolean? = null
    @SerializedName("namerator")
    @Expose
    var namerator: Boolean? = null
    @SerializedName("challenge")
    @Expose
    var challenge: String? = null

    class Deserializer : ResponseDeserializable<Reserve> {
        override fun deserialize(content: String) = Gson().fromJson(content, Reserve::class.java)
    }
}