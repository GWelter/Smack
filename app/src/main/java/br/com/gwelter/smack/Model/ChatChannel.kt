package br.com.gwelter.smack.Model

/**
 * Created by Guilherme on 05/11/2017.
 */
class ChatChannel (val name: String, val description: String, val id: String) {
    override fun toString(): String {
        return "#$name"
    }
}