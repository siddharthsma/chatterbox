package com.chatterbox

/**
 * Created by siddharthambegaonkar on 7/16/14.
 */
abstract class Command
case object ListUsers extends Command
case object ListChannels extends Command
case object ViewOptions extends Command
case object ListSubscribedChannels extends Command
case class CreateChannel(roomName: String, users: Array[String]) extends Command
case class JoinChannel(roomName: String) extends Command
case object LogOutAndCloseConnection extends Command
case object UnknownCommand extends Command

class OptionParser(val command: String) {
  def parse: Command = {
    try {
      val firstWord = command.split(" ")(0)
      firstWord match {
        case "listusers" => ListUsers
        case "listchannels" => ListChannels
        case "mychannels" => ListSubscribedChannels
        case "options" => ViewOptions
        case "createchannel" => createChannelCase(command)
        case "joinchannel" => createJoinChannelCase(command)
        case "quit" => LogOutAndCloseConnection
        case _ => UnknownCommand
      }
    } catch {
      case e: NullPointerException => LogOutAndCloseConnection
    }
  }

  def createChannelCase(command: String) = {
    val splitCommand = command.split(" ")
    val roomName = splitCommand(1)
    val users = splitCommand.drop(2)

    if(roomName.toString != "" && !users.isEmpty) {
      CreateChannel(roomName, users)
    }
    else {
      UnknownCommand
    }
  }

  def createJoinChannelCase(command: String) = {
    /** for now just return UnknownCommand */
    UnknownCommand
  }

}
