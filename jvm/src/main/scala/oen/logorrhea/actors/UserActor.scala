package oen.logorrhea.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import oen.logorrhea.actors.UserActor.{OutMsg, UserMessage, Username, WebsockOutput}

class UserActor extends Actor with ActorLogging {

  override def receive: Receive = emptyActor

  def emptyActor: Receive = {
    case WebsockOutput(out) => context.become(waitingForUsername(out))
  }

  def waitingForUsername(out: ActorRef): Receive = {
    case Username(username) => context.become(handlingMessages(out, username))
    case UserMessage(msg) =>
      val toOut = s"Hello `$msg`"
      log.debug(msg)
      out ! OutMsg(toOut)
  }

  def handlingMessages(out: ActorRef, username: String): Receive = {
    case any =>
      val msg = s"Hello from $username -> $any"
      log.debug(msg)
      out ! msg
  }
}

object UserActor {
  def props = Props(new UserActor)

  case class WebsockOutput(out: ActorRef)
  case class Username(username: String)
  case class UserMessage(msg: String)
  case class OutMsg(msg: String)
}
