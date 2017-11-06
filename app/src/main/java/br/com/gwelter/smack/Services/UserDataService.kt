package br.com.gwelter.smack.Services

import android.graphics.Color
import br.com.gwelter.smack.Controller.App
import java.util.*

/**
 * Created by Guilherme on 04/11/2017.
 */
object UserDataService {
    var id = ""
    var avatarColor = ""
    var avatarName = ""
    var email = ""
    var name = ""

    fun returnAvatarColor(components: String) : Int {
        val strippedColor = components.replace("[", "")
                .replace("]", "")
                .replace(",", "")

        var cor = Color.BLACK

        val scanner = Scanner(strippedColor)
        if(scanner.hasNextDouble()){
            val red = (scanner.nextDouble() * 255).toInt()
            val green = (scanner.nextDouble() * 255).toInt()
            val blue = (scanner.nextDouble() * 255).toInt()
            cor = Color.rgb(red, green, blue)
        }

        return cor
    }

    fun logout() {
        id = ""
        avatarColor = ""
        avatarName = ""
        email = ""
        name = ""

        App.sharedPreferences.authToken = ""
        App.sharedPreferences.userEmail = ""
        App.sharedPreferences.isLogedIn = false

        MessageService.clearMessages()
        MessageService.clearChannels()
    }
}