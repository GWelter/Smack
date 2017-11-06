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
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import br.com.gwelter.smack.Adapters.MessageAdapter
import br.com.gwelter.smack.Model.ChatChannel
import br.com.gwelter.smack.Model.ChatMessage
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
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)

    lateinit var channelAdapter: ArrayAdapter<ChatChannel>
    lateinit var messageAdapter: MessageAdapter
    var selectedChannel : ChatChannel? = null

    private fun setUpAdapter() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter

        messageAdapter = MessageAdapter(this, MessageService.messages)
        messageListView.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
        messageListView.layoutManager = layoutManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReciver, IntentFilter(BROADCAST_USER_DATA_CHANGE))

        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)
        setUpAdapter()

        channel_list.setOnItemClickListener { parent, view, position, id ->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        if(App.sharedPreferences.isLogedIn){
            AuthService.findUserByEmail(this){}
        }
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
            if(App.sharedPreferences.isLogedIn) {
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email

                val resouerceID = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageNavHeader.setImageResource(resouerceID)
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))

                loginButtonNavHeader.text = "Logout"

                MessageService.getChannels { complete ->
                    if(complete) {
                        if(MessageService.channels.count() > 0){
                            selectedChannel = MessageService.channels[0]
                            //Notifica a mudança de dados para o adapter
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }
                    }
                }
            }
        }
    }

    fun updateWithChannel() {
        mainChannelName.text = "#${selectedChannel?.name}"
        //download messages for channel

        if(selectedChannel != null) {
            MessageService.clearMessages()
            MessageService.getMessages(selectedChannel!!.id) { complete ->
                if(complete){
                    messageAdapter.notifyDataSetChanged()
                    if(messageAdapter.itemCount > 0) {
                        messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
            }
        }
    }

    private fun showUserData() {
        if(App.sharedPreferences.isLogedIn) {
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
        if(App.sharedPreferences.isLogedIn) {
            UserDataService.logout()
            channelAdapter.notifyDataSetChanged()
            messageAdapter.notifyDataSetChanged()

            userNameNavHeader.text = "Please login"
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginButtonNavHeader.text = "Login"
            mainChannelName.text = "Please Login"
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelClick(view: View) {
        if(App.sharedPreferences.isLogedIn){
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
        if(App.sharedPreferences.isLogedIn) {
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
    }

    private val onNewMessage = Emitter.Listener { args ->
        if(App.sharedPreferences.isLogedIn) {
            runOnUiThread {
                val channelID = args[2] as String
                if(channelID == selectedChannel?.id) {
                    val msgBody = args[0] as String
                    val userName = args[3] as String
                    val userAvatar = args[4] as String
                    val userAvatarColor = args[5] as String
                    val id = args[6] as String
                    val timestamp = args[7] as String

                    val newMessage = ChatMessage(msgBody, userName, channelID, userAvatar, userAvatarColor, id, timestamp)
                    MessageService.messages.add(newMessage)
                    messageAdapter.notifyDataSetChanged()
                    messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                }
            }
        }
    }

    fun sendMessageButtonClick(view: View) {
        if(App.sharedPreferences.isLogedIn && messageTextField.text.isNotEmpty() && selectedChannel != null) {
            val userID = UserDataService.id
            val channelID = selectedChannel!!.id
            socket.emit("newMessage", messageTextField.text.toString(), userID, channelID, UserDataService.name, UserDataService.avatarName, UserDataService.avatarColor)
            messageTextField.text.clear()
            hideKeyboard()
        }
    }
}
