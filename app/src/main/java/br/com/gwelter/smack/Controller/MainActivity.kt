package br.com.gwelter.smack.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import br.com.gwelter.smack.Model.ChatChannel
import br.com.gwelter.smack.R
import br.com.gwelter.smack.Services.AuthService
import br.com.gwelter.smack.Services.MessageService
import br.com.gwelter.smack.Services.UserDataService
import br.com.gwelter.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import br.com.gwelter.smack.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)

    lateinit var channelAdapter: ArrayAdapter<ChatChannel>

    private fun setUpAdapter() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        socket.connect()
        socket.on("channelCreated", onNewChannel)

        setUpAdapter()
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReciver, IntentFilter(BROADCAST_USER_DATA_CHANGE))
        super.onResume()
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReciver)
        super.onDestroy()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        showUserData()
    }

    private val userDataChangeReciver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if(AuthService.isLoggedIn) {
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email

                val resouerceID = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageNavHeader.setImageResource(resouerceID)
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))

                loginButtonNavHeader.text = "Logout"

                MessageService.getChannels(context) { complete ->
                    if(complete) {
                        //Notifica a mudança de dados para o adapter
                        channelAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun showUserData() {
        if(AuthService.isLoggedIn) {
            userNameNavHeader.text = UserDataService.name
            userEmailNavHeader.text = UserDataService.email

            val resouerceID = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
            userImageNavHeader.setImageResource(resouerceID)
            userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))

            loginButtonNavHeader.text = "Logout"
        }
    }

    private fun hideKeyboard(){
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onBackPressed() {
        hideKeyboard()

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginButtonNavClick(view: View) {
        if(AuthService.isLoggedIn) {
            UserDataService.logout()
            userNameNavHeader.text = "Please login"
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginButtonNavHeader.text = "Login"
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelClick(view: View) {
        if(AuthService.isLoggedIn){
            val alertBuilder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            alertBuilder.setView(dialogView)
                    .setPositiveButton("Add") { dialog, which ->
                        val nameTextField = dialogView.findViewById<EditText>(R.id.channel_name_dialog)
                        val descriptionTextField = dialogView.findViewById<EditText>(R.id.channel_description_dialog)

                        val channelName = nameTextField.text.toString()
                        val channelDescription = descriptionTextField.text.toString()

                        //TODO create channel
                        socket.emit("newChannel", channelName, channelDescription)
                    }
                    .setNegativeButton("Cancel") { dialog, which ->
                    }
                    .show()
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        runOnUiThread {
            //Cast to String pq vem como tipo Any
            val channelName = args[0] as String
            val channelDescription = args[1] as String
            val channelID = args[2] as String

            val newChannel = ChatChannel(channelName, channelDescription, channelID)
            MessageService.channels.add(newChannel)

            channelAdapter.notifyDataSetChanged()
        }
    }

    fun sendMessageButtonClick(view: View) {

    }
}
