package com.chatterbox

import java.io.{PrintStream, BufferedReader}
import akka.actor.{Props, ActorLogging, Actor}

/**
 * Created by siddharthambegaonkar on 7/16/14.
 */
case object UserListRequest
case object ChannelListRequest
case object SubscribedChannelsListRequest
case class ChannelCreationRequest(name: String, users: Array[String])
case object ListenForOptions

class AuthenticatedClient(val inputStream: BufferedReader, val outputStream: PrintStream, val userName: String) extends Actor with ActorLogging {
  def receive = {
    case WelcomeAndWaitForOptions =>
      welcomeMessage
      presentOptions
      listenForOptions
    case ListUsers => requestUserList
    case ListSubscribedChannels => requestSubscribedChannelsList
    case ListChannels => requestChannelsList
    case CreateChannel(roomName, users) => requestChannelCreation(roomName, users)
    case ViewOptions => presentOptions
    case UnknownCommand => badCommand
    case UserListResponse(users) => printList("USERS:",users)
    case ChannelListResponse(channels) => printList("ALL CHANNELS:", channels)
    case SubscribedChannelsListResponse(channels) => printList("MY CHANNELS:", channels)
    case Message(message) => printMessage(message)
  }

  def welcomeMessage = {
    outputStream.println("")
    outputStream.println(s"Welcome to ChatterBox $userName")
    outputStream.println("")
  }

  def presentOptions = {
    outputStream.println("Options")
    outputStream.println("")
    outputStream.println("List Users:         listusers")
    outputStream.println("List Channels:      listchannels")
    outputStream.println("List My Channels:   mychannels")
    outputStream.println("Create Channel:     createchannel [ Channel] [ user1 user2 ... ]")
    outputStream.println("Join Channel:       joinchannel [ Channel ]")
    outputStream.println("View Options:       options")
  }

  def badCommand = {
    outputStream.println("Unknown command or bad syntax")
  }

  def requestUserList = {
    context.actorSelection("akka://chatServerSystem/user/ChatServer/rootHandler") ! UserListRequest
  }

  def requestChannelCreation(roomName: String, users: Array[String]) = {
    context.actorSelection("akka://chatServerSystem/user/ChatServer/rootHandler") ! ChannelCreationRequest(roomName, userName +: users )
  }

  def requestChannelsList = {
    context.actorSelection("akka://chatServerSystem/user/ChatServer/rootHandler") ! ChannelListRequest
  }

  def requestSubscribedChannelsList = {
    context.parent ! SubscribedChannelsListRequest
  }

  def printList(heading: String, users: Set[String]) = {
    outputStream.println(heading)
    users.foreach(user => outputStream.println(user))
  }

  def listenForOptions = {
    val messageListener = context.actorOf(Props(new MessageListener(inputStream)))
    messageListener ! ListenForOptions
  }

  def printMessage(message: String) = {
    outputStream.println(message)
  }
}
