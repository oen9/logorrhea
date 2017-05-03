package oen.logorrhea.actors

import akka.actor.{Actor, ActorLogging, Props, Terminated}
import oen.logorrhea.actors.WebsockDispatcherActor.CreateUser

class WebsockDispatcherActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case CreateUser =>
      val createdUser = context.actorOf(UserActor.props)
      context.watch(createdUser)
      sender() ! createdUser

    case Terminated(actorRef) =>
      log.debug("Terminated {}", actorRef)
  }
}

object WebsockDispatcherActor {
  def props = Props(new WebsockDispatcherActor)

  case object CreateUser
}
