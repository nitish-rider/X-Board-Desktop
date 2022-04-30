package listners

import TyperXView
import TyperXWorker
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

class DiscordApp(private var otp : String): MessageCreateListener,TyperXView  {
    private val threadPool = Executors.newFixedThreadPool(10)
    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    private val typerWorker = TyperXWorker(this)

    override fun onMessageCreate(event: MessageCreateEvent?) {
        if(event != null){
            val message = event.message
            if(message.content.equals(otp,false) && !verification){
                event.channel.sendMessage("Connected")
                verification = true
                channelid = event.channel?.id.toString()
                println("Verification Successfully on channel $channelid")
            }
            else if(message.content.startsWith("#Android->Windows->s", true)){
                message.delete()
                takeSS(event.channel)
            }
            else if(message.content.startsWith("#Android->Windows->c", true)){
                val myString = message.content.substring(20)
                println("Copied $myString into clip board ")
                val stringSelection = StringSelection(myString)
                clipboard.setContents(stringSelection, null)
            }
            else if(message.content.startsWith("#Android->Windows->p", true)){
                val text = message.content.substring(20)
                println("Pasting $text into area removing spaces ")
                typerWorker.isRemoveFrontSpaces = true
                typerWorker.startRequest(text)
            }
        }

    }

    private fun takeSS(channel: TextChannel?) = threadPool.submit {
        println("Taking ss now..")
        MessageBuilder().addAttachment(ImageUtils.getScreenShot(), "ss.jpg").send(channel)
    }

    override fun startUI() {
    }

    override fun stopUI() {
    }

    override fun setProgress(progress: Int) {
    }

    override fun setStatus(status: String?) {
    }
}