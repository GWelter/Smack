package br.com.gwelter.smack.Services

import android.content.Context
import android.util.Log
import br.com.gwelter.smack.Controller.App
import br.com.gwelter.smack.Model.ChatChannel
import br.com.gwelter.smack.Model.ChatMessage
import br.com.gwelter.smack.Utilities.URL_GET_CHANNELS
import br.com.gwelter.smack.Utilities.URL_GET_MESSAGES
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

/**
 * Created by Guilherme on 05/11/2017.
 */
object MessageService {

    val channels = ArrayList<ChatChannel>()
    val messages = ArrayList<ChatMessage>()

    fun getChannels(complete: (Boolean) -> Unit) {
        val channelsRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener { response ->
            try {
                for (x in 0 until response.length()){
                    val channel = response.getJSONObject(x)

                    val name = channel.getString("name")
                    val description = channel.getString("description")
                    val id = channel.getString("_id")

                    MessageService.channels.add(ChatChannel(name, description, id))
                }
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC: " +e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not retrieve channels")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "Application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.sharedPreferences.authToken}")
                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(channelsRequest)
    }

    fun getMessages(channelID: String, complete: (Boolean) -> Unit) {
        val url = "$URL_GET_MESSAGES$channelID"

        val getMessagesRequest = object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
            clearMessages()

            try {
                for(x in 0 until response.length()) {
                    val message = response.getJSONObject(x)
                    val messageBody = message.getString("messageBody")
                    val channelID = message.getString("channelId")
                    val id = message.getString("_id")
                    val userName = message.getString("userName")
                    val userAvatar = message.getString("userAvatar")
                    val userAvatarColor = message.getString("userAvatarColor")
                    val timestamp = message.getString("timeStamp")

                    val newMessage = ChatMessage(messageBody, userName, channelID, userAvatar, userAvatarColor, id, timestamp)
                    this.messages.add(newMessage)
                    complete(true)
                }
            } catch (e: JSONException) {
                Log.d("JSON", "EXC: " +e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not retrieve channels")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "Application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.sharedPreferences.authToken}")
                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(getMessagesRequest)
    }

    fun clearMessages() {
        messages.clear()
    }

    fun clearChannels() {
        channels.clear()
    }
}