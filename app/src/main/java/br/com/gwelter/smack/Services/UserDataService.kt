package br.com.gwelter.smack.Services

import android.graphics.Color
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

        var red = 0
        var green = 0
        var blue = 0

        println(strippedColor)

        val scanner = Scanner(strippedColor)
        if(scanner.hasNextDouble()){
            red = (scanner.nextDouble() * 255).toInt()
            green = (scanner.nextDouble() * 255).toInt()
            blue = (scanner.nextDouble() * 255).toInt()
        }

        return Color.rgb(red, green, blue)
    }

    fun logout() {
        id = ""
        avatarColor = ""
        avatarName = ""
        email = ""
        name = ""

        AuthService.authToken = ""
        AuthService.userEmail = ""
        AuthService.isLoggedIn = false
    }
}