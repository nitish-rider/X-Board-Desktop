package login

import kotlin.random.Random

class Login {
    public fun generateOtp() : String{
        val otp = Random.nextInt(1000, 9999).toString()
        println(
            "Welcome to X-Board :) \n" +
                    "First step is to connect your devices" +
                    "\nand enter this otp $otp in the android app!!!!" +
                    "\n\n" +
                    "Waiting for Events.....".trimIndent()
        )
        return otp
    }
}