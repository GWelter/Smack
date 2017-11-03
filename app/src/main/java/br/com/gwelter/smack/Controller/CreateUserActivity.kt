package br.com.gwelter.smack.Controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.com.gwelter.smack.R
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun generateUserAvatar(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        userAvatar = if(color == 0){
            "light$avatar"
        }else{
            "dark$avatar"
        }

        val resourceID = resources.getIdentifier(userAvatar, "drawable", packageName)
        createAvatarImageview.setImageResource(resourceID)
    }

    fun generateColorClick(view: View) {
        val random = Random()
        val red = random.nextInt(255)
        val green = random.nextInt(255)
        val blue = random.nextInt(255)

        createAvatarImageview.setBackgroundColor(Color.rgb(red, green, blue))
        val savedRed = red.toDouble() / 255
        val savedGreen = green.toDouble() / 255
        val savedBlue = blue.toDouble() / 255

        avatarColor = "[$savedRed, $savedGreen, $savedBlue, 1]"
    }

    fun createUserClick(view: View) {

    }
}
