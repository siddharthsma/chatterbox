package com.chatterbox

import java.io.{PrintStream, BufferedReader}
import akka.actor.{Props, ActorLogging, Actor}

/**
 * Created by siddharthambegaonkar on 7/16/14.
 */
case object UserListRequest
case object ChannelListRequest
case object SubscribedChannelsListRequest
case class ChannelCreationRequest(name: String, user: String)
case class ChannelSubscriptionRequest(name: String, user: String)
case class ChannelUnSubscriptionRequest(name: String, user: String)
case class DeliverMessage(channelName: String, message: String, from: String)
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
    case CreateChannel(channelName) => requestChannelCreation(channelName)
    case JoinChannel(channelName) => requestChannelSubscription(channelName)
    case LeaveChannel(channelName) => requestChannelUnSubscription(channelName)
    case ViewOptions => presentOptions
    case UnknownCommand => badCommand
    case UserListResponse(users) => printList("USERS:",users)
    case ChannelListResponse(channels) => printList("ALL CHANNELS:", channels)
    case SubscribedChannelsListResponse(channels) => printList("MY CHANNELS:", channels)
    case SendMessage(channelName, message) => sendMessage(channelName, message)
    case Message(message) => printMessage(message)
  }

  def welcomeMessage = {
    outputStream.println("")
    outputStream.println(s"Welcome to ChatterBox $userName")
    outputStream.println("")
  }

  def presentOptions = {
    outputStream.println("OPTIONS")
    outputStream.println("")
    outputStream.println("List Users:         listusers")
    outputStream.println("List Channels:      listchannels")
    outputStream.println("List My Channels:   mychannels")
    outputStream.println("Create Channel:     createchannel [ Channel]")
    outputStream.println("Join Channel:       joinchannel [ Channel ]")
    outputStream.println("Leave Channel:      leavechannel [ Channel ]")
    outputStream.println("Send Message:       send [ Channel ] [ Message ]")
    outputStream.println("View Options:       options")
  }

  def badCommand = {
    outputStream.println("Unknown command or bad syntax")
  }

  def requestUserList = {
    context.actorSelection("akka://chatServerSystem/user/ChatServer/rootHandler") ! UserListRequest
  }

  def requestChannelCreation(channelName: String) = {
    context.actorSelection("akka://chatServerSystem/user/ChatServer/rootHandler") ! ChannelCreationRequest(channelName, userName )
  }

  def requestChannelSubscription(channelName: String) = {
    context.actorSelection("akka://chatServerSystem/user/ChatServer/rootHandler") ! ChannelSubscriptionRequest(channelName, userName)
  }

  def requestChannelUnSubscription(channelName: String) = {
    context.actorSelection("akka://chatServerSystem/user/ChatServer/rootHandler") ! ChannelUnSubscriptionRequest(channelName, userName)
  }

  def requestChannelsList = {
    context.actorSelection("akka://chatServerSystem/user/ChatServer/rootHandler") ! ChannelListRequest
  }

  def sendMessage(channelName: String, message: String) = {
    context.parent ! DeliverMessage(channelName, message, userName)
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
