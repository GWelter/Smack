package br.com.gwelter.smack.Controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import br.com.gwelter.smack.R
import br.com.gwelter.smack.Services.AuthService
import br.com.gwelter.smack.Services.UserDataService
import br.com.gwelter.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createSpinner.visibility = View.INVISIBLE
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
        enableSpinner(true)

        val userName = createUserNameText.text.toString()
        val password = createPasswordText.text.toString()
        val email = createEmailText.text.toString()

        if(userName.isNotEmpty() && password.length > 5 && email.isNotEmpty()) {

            AuthService.registerUser(this, email, password) { completeRegistration ->

                if (completeRegistration) {
                    AuthService.loginUser(this, email, password) { completeLogin ->

                        if (completeLogin) {
                            AuthService.createUser(this, userName, email, userAvatar, avatarColor) { completeCreation ->

                                if (completeCreation) {

                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)

                                    enableSpinner(false)
                                    finish()
                                } else {
                                    errorToast()
                                }
                            }
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        } else {
            Toast.makeText(this, "Make sure user name, email, and password are filled", Toast.LENGTH_SHORT).show()
            enableSpinner(false)
        }
    }

    fun errorToast() {
        Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean) {
        if(enable) {
            createSpinner.visibility = View.VISIBLE
        } else {
            createSpinner.visibility = View.INVISIBLE
        }

        createUserButton.isEnabled = !enable
        createAvatarImageview.isEnabled = !enable
        backgroundColorButton.isEnabled = !enable
    }
}
