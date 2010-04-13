package Chat

object ChatMain {
  def main(args: Array[String]){
    var bot = new ChatBot()
    
    bot.setVerbose(true)
    bot.connect("irc.freenode.net")
    bot.joinChannel("#pircbot")
  }
}
