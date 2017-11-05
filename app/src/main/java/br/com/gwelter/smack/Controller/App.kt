package br.com.gwelter.smack.Controller

import android.app.Application
import br.com.gwelter.smack.Utilities.SharedPrefs

/**
 * Created by Guilherme on 05/11/2017.
 */
class App : Application() {

    companion object {
        lateinit var sharedPreferences: SharedPrefs
    }

    override fun onCreate() {
        sharedPreferences = SharedPrefs(applicationContext)
        super.onCreate()
    }
}