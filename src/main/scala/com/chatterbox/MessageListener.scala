package com.chatterbox

import akka.actor.{Actor,ActorLogging}
import java.io.BufferedReader

/**
 * Created by siddharthambegaonkar on 7/16/14.
 */

class MessageListener(inputStream: BufferedReader) extends Actor with ActorLogging{
  def receive = {
    case ListenForOptions => waitForOptions
  }

  def waitForOptions = {
    while (true) {
      sender ! new OptionParser(inputStream.readLine()).parse
    }
  }


}
