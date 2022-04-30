package listners

import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener
import utils.API.channelid
import utils.API.verification
import utils.ImageUtils
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.concurrent.Executors

class MyMessageListener(private val otp : String): MessageCreateListener {
    private val threadPool = Executors.newFixedThreadPool(10)
    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard

    override fun onMessageCreate(event: MessageCreateEvent?) {
        if(event != null){
            val message = event.message
            if(message.content.equals(otp,false)){
                message.delete()
                event.channel.sendMessage("Touch the Camera emoji in this message to take a ss")
                verification = true
                channelid = event.channel?.id.toString()
                println("Verification Successfully on channel $channelid")
            }
            else if(message.content.length  == 1){
                message.delete()
                takeSS(event.channel)
            }
            else if(message.content.startsWith("#c", true)){
                val myString = message.content.substring(2)
                println("Copied $myString into clip board ")
                val stringSelection = StringSelection(myString)
                clipboard.setContents(stringSelection, null)
            }
        }

    }

    private fun takeSS(channel: TextChannel?) = threadPool.submit {
        println("Taking ss now..")
        MessageBuilder().addAttachment(ImageUtils.getScreenShot(), "ss.jpg").send(channel)
    }
}