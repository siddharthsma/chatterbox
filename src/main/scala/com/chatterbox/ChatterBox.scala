package com.chatterbox

import java.io.{InputStreamReader, PrintStream, BufferedReader}
import java.net.{ServerSocket, Socket}
import akka.actor.{Props, ActorSystem}

/**
 * Created by siddharthambegaonkar on 7/14/14.
 */
case class StartServer(port: Int)

object ChatterBox extends App{

  /** TODO: give the user various options to control and see what is happening on the server */
  override def main(args: Array[String]): Unit = {
    val chatServerSystem = ActorSystem("chatServerSystem")
    val chatServer = chatServerSystem.actorOf(Props(new ChatServer), "ChatServer")
    chatServer ! StartServer(3636)
  }

}
