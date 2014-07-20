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
case object ChannelSubscriptionMessageToAll

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
    case ChannelCreationRequest(roomName, users) =>
      val channelUsersMap = actorRefsOfUsers(users)
      createChannel(roomName, channelUsersMap)
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

  def actorRefsOfUsers(users: Array[String]) = {
    usersMap.filterKeys(users.contains)
  }

  def createChannel(name: String, subscribersMap: Map[String, ActorRef]) = {
    val channel = context.actorOf(Props(new Channel(name, subscribersMap)), name)
    channel ! ChannelSubscriptionMessageToAll
    channelsMap = channelsMap ++ Map(name -> channel)
  }
}