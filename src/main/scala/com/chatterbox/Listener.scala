package com.chatterbox

import akka.actor.{Actor,ActorLogging}
import java.net.{Socket,ServerSocket}

/**
 * Created by siddharthambegaonkar on 7/15/14.
 */
case class ConnectedClient(socket: Socket)

class Listener extends Actor with ActorLogging{

  def receive = {
    case ListenForConnections(serverSocket) =>
      keepListening(serverSocket)
  }

  def keepListening(serverSocket: ServerSocket) = {
    while (true) {
      val socket = serverSocket.accept()
      context.parent ! ConnectedClient(socket)
    }
  }

}
