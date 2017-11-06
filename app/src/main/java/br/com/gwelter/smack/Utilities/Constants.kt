package br.com.gwelter.smack.Utilities

/**
 * Created by guilherme on 02/11/17.
 */
const val BASE_URL = "https://welterchatsmack.herokuapp.com/v1/"
const val SOCKET_URL = "https://welterchatsmack.herokuapp.com/"

const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}account/login"
const val URL_CREATE_USER = "${BASE_URL}user/add"
const val URL_GET_USER = "${BASE_URL}user/byEmail/"

const val URL_GET_CHANNELS = "${BASE_URL}channel"
const val URL_GET_MESSAGES = "${BASE_URL}message/byChannel/"

//Broadcast Constant
const val BROADCAST_USER_DATA_CHANGE = "Broadcast_user_data_change"