package oen.logorrhea.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import oen.logorrhea.actors.UserActor.WebsockOutput
import oen.logorrhea.models._

class UserActor(userListActor: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = emptyActor

  def emptyActor: Receive = {
    case WebsockOutput(out) => context.become(waitingForUsername(out))
  }

  def waitingForUsername(out: ActorRef): Receive = {
    case u: Username =>
      context.become(handlingMessages(out, u.username))

      context.system.eventStream.subscribe(self, classOf[Message])
      context.system.eventStream.subscribe(self, classOf[UserAdded])
      context.system.eventStream.subscribe(self, classOf[UserRemoved])

      userListActor ! UserListActor.GetUsers
      userListActor ! UserAdded(u)
  }

  def handlingMessages(out: ActorRef, username: String): Receive = {
    case inMsg @ Message(_, _, None) =>
      context.system.eventStream.publish(inMsg.copy(from = Some(username)))

    case outMsg: Message => out ! outMsg
    case added: UserAdded => out ! added
    case removed: UserRemoved => out ! removed
    case ul: UserList => out ! ul
  }
}

object UserActor {
  def props(userListActor: ActorRef) = Props(new UserActor(userListActor))

  case class WebsockOutput(out: ActorRef)
}
