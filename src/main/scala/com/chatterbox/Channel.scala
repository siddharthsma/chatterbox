package com.chatterbox

import akka.actor.{ActorRef, Actor, ActorLogging}

/**
 * Created by siddharthambegaonkar on 7/19/14.
 */
case class Message(message: String)
case class ChannelGreeting(message: String, channelNameAndRef: (String, ActorRef) )

class Channel(name: String, subscribersMap: Map[String, ActorRef]) extends Actor with ActorLogging{
  def receive = {
    case ChannelSubscriptionMessageToAll => channelMessage(s"Joined Channel $name", subscribersMap.keySet)
  }

  def channelMessage(message: String, subscriberNames: Set[String]) = {
    val subscriberNamesAndRefs = getParticularSubscribersMap(subscriberNames)
    subscriberNamesAndRefs.foreach((e: (String, ActorRef)) => e._2 ! ChannelGreeting(s"$name : Joined channel $name", (name, context.self)))
  }

  def getParticularSubscribersMap(subscriberNames: Set[String]) = {
    subscribersMap.filterKeys(subscriberNames.contains)
  }
}
