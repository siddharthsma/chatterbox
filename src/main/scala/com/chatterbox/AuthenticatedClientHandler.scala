package com.chatterbox

import akka.actor.{ActorRef, Props, Actor, ActorLogging}
import java.net.Socket
import java.io.{InputStreamReader, PrintStream, BufferedReader}

/**
 * Created by siddharthambegaonkar on 7/15/14.
 */
case object WelcomeAndWaitForOptions
case class SubscribedChannelsListResponse(myChannels: Set[String])
case class ChannelMessage(message: String, from: String)

class AuthenticatedClientHandler(val socket: Socket) extends Actor with ActorLogging {
  var myChannelsMap = Map[String, ActorRef]()

  def receive = {
    case StartHandlingClient(userName) =>
      val (inputStream, outputStream) = setupClientStreams(socket)
      val client = createClientActor(inputStream, outputStream, userName)
      client ! WelcomeAndWaitForOptions
    case ChannelSubscriptionResponse(channelName, channelRef) =>
      addToSubscribedChannels(channelName, channelRef)
    case ChannelUnSubscriptionResponse(channelName, channelRef) =>
      removeFromSubscribedChannels(channelName, channelRef)
      forwardMessage(s"Unsubscribed from channel $channelName")
    case ChannelDoesNotExist(channelName) =>
      forwardMessage(s"Channel $channelName does not exist")
    case Message(message) => forwardMessage(message)
    case DeliverMessage(channelName, message, from) =>
      if(validChannel(channelName)) deliverMessage(channelName, message, from) else forwardMessage("Invalid channel")
    case SubscribedChannelsListRequest => sender ! SubscribedChannelsListResponse(myChannelsMap.keySet)
  }

  def setupClientStreams(clientSocket: Socket): (BufferedReader, PrintStream) = {
    val inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream))
    val outputStream = new PrintStream(clientSocket.getOutputStream)
    (inputStream, outputStream)
  }

  def createClientActor(inputStream: BufferedReader, outputStream: PrintStream, userName: String) = {
    context.actorOf(Props(new AuthenticatedClient(inputStream = inputStream, outputStream = outputStream, userName = userName)))
  }

  def forwardMessage(message: String) = {
    context.children.last ! Message(message)
  }

  def addToSubscribedChannels(channelName: String, channelRef: ActorRef) = {
    myChannelsMap = Map(channelName -> channelRef) ++ myChannelsMap
  }

  def removeFromSubscribedChannels(channelName: String, channelRef: ActorRef) = {
    myChannelsMap = myChannelsMap - channelName
  }

  def validChannel(channelName: String) = {
    myChannelsMap.contains(channelName)
  }

  def deliverMessage(channelName: String, message: String, from: String) = {
    myChannelsMap(channelName) ! ChannelMessage(message, from)
  }
}