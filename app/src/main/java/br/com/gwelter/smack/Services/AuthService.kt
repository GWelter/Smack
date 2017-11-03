package br.com.gwelter.smack.Services

import android.content.Context
import android.util.Log
import br.com.gwelter.smack.Utilities.URL_REGISTER
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject

/**
 * Created by guilherme on 02/11/17.
 */
object AuthService {

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(Method.POST, URL_REGISTER, Response.Listener { response ->
            println(response)
            complete(true)
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "could not register user: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "Application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
    }
}