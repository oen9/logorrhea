package oen.logorrhea.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import oen.logorrhea.actors.UserActor.WebsockOutput
import oen.logorrhea.models.{Message, Username}

class UserActor extends Actor with ActorLogging {

  override def receive: Receive = emptyActor

  context.system.eventStream.subscribe(self, classOf[Message])

  def emptyActor: Receive = {
    case WebsockOutput(out) => context.become(waitingForUsername(out))
  }

  def waitingForUsername(out: ActorRef): Receive = {
    case Username(username) => context.become(handlingMessages(out, username))
  }

  def handlingMessages(out: ActorRef, username: String): Receive = {
    case inMsg @ Message(_, _, None) =>
      context.system.eventStream.publish(inMsg.copy(from = Some(username)))
    case outMsg: Message =>
      out ! outMsg
  }
}

object UserActor {
  def props = Props(new UserActor)

  case class WebsockOutput(out: ActorRef)
}
