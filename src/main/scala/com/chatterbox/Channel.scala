package com.chatterbox

import akka.actor.{ActorRef, Actor, ActorLogging}

/**
 * Created by siddharthambegaonkar on 7/19/14.
 */
case class Message(message: String)
case class ChannelGreeting(message: String, channelNameAndRef: (String, ActorRef) )
case class ChannelSubscriptionResponse(channelName: String, channelRef: ActorRef)
case class ChannelUnSubscriptionResponse(channelName: String, channelRef: ActorRef)

class Channel(name: String) extends Actor with ActorLogging{
  var subscribersMap = Map[String, ActorRef]()

  def receive = {
    case ChannelSubscriptionMessage(userAndRef) =>
      addToSubscribersMap(userAndRef)
      sendSubscriptionConfirmation(userAndRef)
      val user = userAndRef._1
      sendMessage(s"$name: $user joined channel")
    case ChannelUnSubscriptionMessage(userAndRef) =>
      removeFromSubscribersMap(userAndRef)
      sendUnSubscriptionConfirmation(userAndRef)
      val user = userAndRef._1
      sendMessage(s"$name: $user left channel ")
    case ChannelMessage(message, from) => sendMessage(s"$name:$from: $message")
  }

  def addToSubscribersMap(userAndRef: (String, ActorRef)) = {
    subscribersMap = subscribersMap ++ Map(userAndRef._1 -> userAndRef._2)
  }

  def removeFromSubscribersMap(userAndRef: (String, ActorRef)) = {
    subscribersMap = subscribersMap - userAndRef._1
  }

  def sendSubscriptionConfirmation(userAndRef: (String, ActorRef)) = {
    userAndRef._2 ! ChannelSubscriptionResponse(name, context.self)
  }

  def sendUnSubscriptionConfirmation(userAndRef: (String, ActorRef)) = {
    userAndRef._2 ! ChannelUnSubscriptionResponse(name, context.self)
  }

  def sendMessage(message: String) = {
    subscribersMap.values.foreach(ref => ref ! Message(message))
  }

}
