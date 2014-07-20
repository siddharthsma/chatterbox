package com.chatterbox

import akka.actor.{Props, ActorSystem, Actor, ActorLogging}
import java.net.{Socket, ServerSocket}

/**
 * Created by siddharthambegaonkar on 7/15/14.
 */

case class HandleConnections(serverSocket: ServerSocket)

class ChatServer extends Actor with ActorLogging{

  /** TODO other cases eg. StopServer */
  def receive = {
    case StartServer(port) =>
      handleConnections(port)
  }

  def handleConnections(port: Int) = {
    val serverSocket = new ServerSocket(port)
    val rootHandler = context.actorOf(Props(new RootHandler), "rootHandler")
    rootHandler ! HandleConnections(serverSocket)
  }


}
