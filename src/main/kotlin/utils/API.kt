package utils

import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder

class API {
    val api: DiscordApi by lazy {
        val api = DiscordApiBuilder().setToken(CONSTANTS.BOT_KEY).login().join()
        api
    }
}