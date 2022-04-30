import listners.MyMessageListener
import login.Login
import utils.API.api

fun main() {
        val generateOtp = Login().generateOtp()
        api.addListener(MyMessageListener(generateOtp))
}
