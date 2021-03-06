package com.chatterbox

import akka.actor._
import java.net.{Socket, ServerSocket}

/**
 * Created by siddharthambegaonkar on 7/15/14.
 */
case class StartHandlingClient(userName: String)
case class ListenForConnections(serverSocket: ServerSocket)
case class UserListResponse(users: Set[String])
case class ChannelListResponse(channels: Set[String])
case class ChannelSubscriptionMessage(userAndRef: (String, ActorRef))
case class ChannelUnSubscriptionMessage(userAndRef: (String, ActorRef))
case class ChannelDoesNotExist(channelName: String)

class RootHandler extends Actor with ActorLogging {
  var usersMap = Map[String, ActorRef]()
  var channelsMap = Map[String, ActorRef]()

  def receive = {
    case HandleConnections(serverSocket) =>
      launchListener(serverSocket)
    case ConnectedClient(socket) =>
      assignUnAuthenticatedClientHandler(socket)
    case AuthorizedClient(userName, socket) =>
      assignAuthenticatedClientHandler(userName, socket)
    case UserListRequest =>
      sendListOfUsers
    case ChannelListRequest =>
      sendListOfChannels
    case ChannelCreationRequest(channelName, user) =>
      createChannel(channelName)
      joinChannel(channelName, userAndRef(user))
    case ChannelSubscriptionRequest(channelName, user) =>
      if (channelExists(channelName)) joinChannel(channelName, userAndRef(user)) else sendChannelDoesNotExist(channelName, user)
    case ChannelUnSubscriptionRequest(channelName, user) =>
      if (channelExists(channelName)) leaveChannel(channelName, userAndRef(user)) else sendChannelDoesNotExist(channelName, user)
  }

  def launchListener(serverSocket: ServerSocket) = {
    val listener = context.actorOf(Props[Listener])
    listener ! ListenForConnections(serverSocket)
  }

  def assignUnAuthenticatedClientHandler(socket: Socket) = {
    val unAuthenticatedClientHandler = context.actorOf(Props(new UnAuthenticatedClientHandler(socket)))
    unAuthenticatedClientHandler ! StartHandlingClient
  }

  def assignAuthenticatedClientHandler(userName: String, socket: Socket) = {
    val authenticatedClientHandler = context.actorOf(Props(new AuthenticatedClientHandler(socket)), userName)
    usersMap = usersMap ++ Map(userName -> authenticatedClientHandler)
    authenticatedClientHandler ! StartHandlingClient(userName)
  }

  def sendListOfUsers = {
    val usersList = usersMap.keySet
    sender ! UserListResponse(usersList)
  }

  def sendListOfChannels = {
    val channelList = channelsMap.keySet
    sender ! ChannelListResponse(channelList)
  }

  def sendChannelDoesNotExist(channelName: String, user: String) = {
    usersMap(user) ! ChannelDoesNotExist(channelName)
  }

  def userAndRef(user: String) = {
    (user, usersMap(user))
  }

  def createChannel(name: String) = {
    val channel = context.actorOf(Props(new Channel(name)), name)
    channelsMap = channelsMap ++ Map(name -> channel)
  }

  def joinChannel(name: String, userAndRef: (String, ActorRef)) = {
    val channel = channelsMap(name)
    channel ! ChannelSubscriptionMessage(userAndRef)
  }

  def leaveChannel(name: String, userAndRef: (String, ActorRef)) = {
    val channel = channelsMap(name)
    channel ! ChannelUnSubscriptionMessage(userAndRef)
  }

  def channelExists(channelName: String): Boolean = {
    channelsMap.keySet.contains(channelName)
  }
}