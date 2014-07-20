package com.chatterbox

import akka.actor.{Actor,ActorLogging}
import java.net.Socket
import java.io.{PrintStream, BufferedReader}
import scala.util.{Failure, Success}

/**
 * Created by siddharthambegaonkar on 7/14/14.
 */
case object CloseClientHandler
case class AuthenticatedAs(userName: String)

class UnAuthenticatedClient(val inputStream: BufferedReader, val outputStream: PrintStream) extends Actor with ActorLogging{
  def receive = {
    case RequestAuthentication =>
      authenticateClient
  }

  def authenticateClient = {
    outputStream.println("Enter Authentication token: ")
    new AuthenticationService(inputStream.readLine()).authenticate match {
      case Success(userName) =>
        sender ! AuthenticatedAs(userName)
        context.stop(self)
      case Failure(ex) =>
        sender ! CloseClientHandler
    }
  }


}
