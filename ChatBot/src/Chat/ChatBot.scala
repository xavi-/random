package Chat

import org.jibble.pircbot._

class ChatBot extends PircBot{
  this.setName("Celluar-Automaton-Bot")
  
  override def onMessage(channel: String, sender: String, 
                         login: String, hostname: String, message: String)
  {
    this.sendMessage(channel, message)
  }
}
