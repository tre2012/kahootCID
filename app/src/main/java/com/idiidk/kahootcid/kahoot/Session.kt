package com.idiidk.kahootcid.kahoot

import org.cometd.client.BayeuxClient
import org.cometd.client.transport.LongPollingTransport
import org.eclipse.jetty.client.HttpClient

class Session(name:String, pin:Int, session:String, twoFactor:Boolean) {
    val pin = pin
    val name = name
    val cometdUrl = "https://kahoot.it/cometd"
    var client:BayeuxClient

    init {
        val httpClient = HttpClient()
        httpClient.start()
        client = BayeuxClient(this.cometdUrl+ "/" + pin + "/" + session, LongPollingTransport(null, httpClient))
    }

    fun connect(callback: (success: Boolean) -> Unit) {
        client.handshake()
        val success = client.waitFor(5000, BayeuxClient.State.CONNECTED)
        if(success) {
            val data = HashMap<String, String>()
            data.put("gameid", this.pin.toString())
            data.put("name", this.name)
            data.put("type", "login")

            this.sendRaw("/service/controller", data)
        }
        callback(success)
    }

    fun sendRaw(channel:String, data:HashMap<String, String>) {
        data.put("host", "play.kahoot.it")
        data.put("room", "/chat/demo")
        client.getChannel(channel).publish(data)
    }
}
