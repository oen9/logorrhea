package oen.logorrhea.actors

import akka.actor.{Actor, ActorLogging, Props, Terminated}
import oen.logorrhea.actors.WebsockDispatcherActor.CreateUser

class WebsockDispatcherActor extends Actor with ActorLogging {

  val userListActor = context.actorOf(UserListActor.props, UserListActor.name)
  context.watch(userListActor)

  override def receive: Receive = {
    case CreateUser =>
      val createdUser = context.actorOf(UserActor.props(userListActor))
      context.watch(createdUser)
      sender() ! createdUser

    case Terminated(`userListActor`) =>
      log.error("{} terminated, restarting WebsockDispatcherActor", UserListActor.name)
      // TODO restart WebsockDispatcherActor

    case Terminated(actorRef) =>
      log.debug("Terminated {}", actorRef)
  }
}

object WebsockDispatcherActor {
  def props = Props(new WebsockDispatcherActor)
  def name: String = "websock-dispatcher"

  case object CreateUser
}
