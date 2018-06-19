package com.idiidk.kahootcid.kahoot

import android.text.TextUtils
import android.util.Base64
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import org.mariuszgromada.math.mxparser.Expression

private const val apiUrl = "https://www.kahoot.it"

class Kahoot(name: String, pin: Int) {
    val pin = pin
    val name = name

    fun connect(callback: (session: Session?) -> Unit) {
        Fuel.get("$apiUrl/reserve/session/$pin/")
                .responseObject(Reserve.Deserializer()) { _, response, result ->
                    result.success {
                        val (data, _) = result
                        val rawSession = response.headers.get("x-kahoot-session-token")?.get(0)
                                ?: String.toString()
                        val solvedChallenge = solveChallenge(data!!.challenge!!)
                        val session = shiftBits(rawSession, solvedChallenge)
                        val twoFactor = data.twoFactorAuth!!

                        val kahootSession = Session(name, pin, session, twoFactor)
                        kahootSession.connect { success ->
                            if (!success) {
                                callback(null)
                            } else {
                                callback(kahootSession)
                            }
                        }
                    }

                    result.failure {
                        callback(null)
                    }
                }
    }

    private fun shiftBits(session: String, challenge: String): String {
        val sessionBytes = this.convertDataToBinary(String(Base64.decode(session, 0)))
        val challengeBytes = this.convertDataToBinary(challenge)
        val bytesList = ArrayList<String>()
        for (i in 0 until sessionBytes.size) {
            bytesList.add(fromCharCode(sessionBytes[i] xor challengeBytes[i % challengeBytes.size]))
        }
        return TextUtils.join("", bytesList)
    }

    private fun convertDataToBinary(raw: String): ArrayList<Int> {
        val tempArray = ArrayList<Int>()

        for (i in 0 until raw.length) {
            tempArray.add(raw[i].toInt())
        }

        return tempArray
    }

    private fun solveChallenge(challenge: String): String {
        var toDecode = challenge.split("'")[1].split("'")[0]
        val offset = Expression(challenge.split("var offset = ")[1].split(";")[0]).calculate()
        val decodeMod = Integer.parseInt(challenge.split(") % ")[1].split(")")[0].trim())
        val decodePlus = Integer.parseInt(challenge.split(decodeMod.toString())[1].split("+ ")[1].split(")")[0])
        var final = ""
        var i = 0


        for (char in toDecode) {
            final += fromCharCode(((((char.toInt() * i) + offset) % decodeMod) + decodePlus).toInt())
            i++
        }

        return final
    }

    private fun fromCharCode(vararg codePoints: Int): String {
        return String(codePoints, 0, codePoints.size)
    }
}

fun checkPin(pin: Int, callback: (exists: Boolean) -> Unit) {
    Fuel.get("$apiUrl/reserve/session/$pin/")
            .responseObject(Reserve.Deserializer()) { _, _, result ->
                result.success {
                    callback(true)
                }

                result.failure {
                    callback(false)
                }
            }
}