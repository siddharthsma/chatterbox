package com.chatterbox

/**
 * Created by siddharthambegaonkar on 7/16/14.
 */
abstract class Command
case object ListUsers extends Command
case object ListChannels extends Command
case object ViewOptions extends Command
case object ListSubscribedChannels extends Command
case class CreateChannel(channelName: String) extends Command
case class JoinChannel(channelName: String) extends Command
case class LeaveChannel(channelName: String) extends Command
case class SendMessage(channelName: String, message: String) extends Command
case object LogOutAndCloseConnection extends Command
case object UnknownCommand extends Command
case object DoNothing extends Command

class OptionParser(val command: String) {
  def parse: Command = {
    try {
      val firstWord = command.split(" ")(0)
      firstWord match {
        case "listusers" => ListUsers
        case "listchannels" => ListChannels
        case "mychannels" => ListSubscribedChannels
        case "options" => ViewOptions
        case "createchannel" => createCreateChannelCase(command)
        case "joinchannel" => joinChannelCase(command)
        case "leavechannel" => leaveChannelCase(command)
        case "send" => sendMessageCase(command)
        case "quit" => LogOutAndCloseConnection
        case "" => DoNothing
        case _ => UnknownCommand
      }
    } catch {
      case e: NullPointerException => LogOutAndCloseConnection
    }
  }

  def createCreateChannelCase(command: String) = {
    val splitCommand = command.split(" ")

    if(splitCommand.length > 1) {
      val channelName = splitCommand(1)
      CreateChannel(channelName)
    }
    else {
      UnknownCommand
    }
  }

  def joinChannelCase(command: String) = {
    val splitCommand = command.split(" ")

    if(splitCommand.length > 1) {
      val channelName = splitCommand(1)
      JoinChannel(channelName)
    }
    else {
      UnknownCommand
    }
  }

  def leaveChannelCase(command: String) = {
    val splitCommand = command.split(" ")

    if(splitCommand.length > 1) {
      val channelName = splitCommand(1)
      LeaveChannel(channelName)
    }
    else {
      UnknownCommand
    }
  }

  def sendMessageCase(command: String) = {
    val splitCommand = command.split(" ", 3)

    if(splitCommand.length == 3) {
      val channelName = splitCommand(1)
      val message = splitCommand(2)
      SendMessage(channelName, message)
    }
    else {
      UnknownCommand
    }


  }
}
