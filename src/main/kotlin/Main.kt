import listners.DiscordApp
import login.Login
import utils.API.api

fun main() {
        val generateOtp = Login().generateOtp()
        api.addListener(DiscordApp(generateOtp))
}
