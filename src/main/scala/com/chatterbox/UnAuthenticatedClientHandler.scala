package com.chatterbox

import akka.actor.{Props, ActorSystem, Actor, ActorLogging}
import java.net.Socket
import java.io.{InputStreamReader, PrintStream, BufferedReader}

/**
 * Created by siddharthambegaonkar on 7/14/14.
 */
case object RequestAuthentication
case class AuthorizedClient(userName: String, socket: Socket)

class UnAuthenticatedClientHandler(val socket: Socket) extends Actor with ActorLogging{

  def receive = {
    case StartHandlingClient =>
      val (inputStream, outputStream) = setupClientStreams(socket)
      val client = createClientActor(inputStream, outputStream)
      client ! RequestAuthentication
    case AuthenticatedAs(userName) =>
      registerWithRootHandler(userName)
      closeClientHandler
    case CloseClientHandler =>
      closeClientConnection
  }

  def setupClientStreams(clientSocket: Socket): (BufferedReader, PrintStream) = {
    val inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream))
    val outputStream = new PrintStream(clientSocket.getOutputStream)
    (inputStream, outputStream)
  }

  def createClientActor(inputStream: BufferedReader, outputStream: PrintStream) = {
    context.actorOf(Props(new UnAuthenticatedClient(inputStream = inputStream, outputStream = outputStream)))
  }

  def registerWithRootHandler(userName: String) = {
    context.parent ! AuthorizedClient(userName, socket)
  }

  def closeClientHandler = {
    context.stop(self)
  }

  def closeClientConnection = {
    socket.close()
    closeClientHandler
  }
}
