package oen.logorrhea.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import oen.logorrhea.actors.WebsockDispatcherActor.CreateUser

class WebsockDispatcherActor extends Actor with ActorLogging {

  val userListActor: ActorRef = context.actorOf(UserListActor.props, UserListActor.name)
  context.watch(userListActor)

  val roomsActor: ActorRef = context.actorOf(RoomsActor.props)

  override def receive: Receive = {
    case CreateUser =>
      val createdUser = context.actorOf(UserActor.props(userListActor, roomsActor))
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
