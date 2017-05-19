package oen.logorrhea.actors

import akka.actor.{Actor, ActorRef, Props, Terminated}
import oen.logorrhea.actors.RoomActor.{GetMessages, Join, Quit}
import oen.logorrhea.models.{Message, Messages}

class RoomActor(name: String) extends Actor {

  var users: Set[ActorRef] = Set()
  var messages: Vector[Message] = Vector()

  override def receive: Receive = {
    case Join =>
      users = users + sender()
      sender() ! Messages(messages)
      context.watch(sender())

    case Quit =>
      users = users - sender()

    case Terminated(actorRef) =>
      users = users - actorRef

    case msg: Message =>
      messages = messages :+ msg
      users.foreach(_ ! msg)
      if (messages.size > 300) messages = messages.drop(150)

    case GetMessages =>
      sender() ! Messages(messages)
  }

  override def postStop(): Unit = {
    users.foreach(_ ! Message("Room deleted!", name, Some(name)))
    super.postStop()
  }
}

object RoomActor {
  def props(name: String) = Props(new RoomActor(name))

  case object Join
  case object Quit
  case object GetMessages
}
