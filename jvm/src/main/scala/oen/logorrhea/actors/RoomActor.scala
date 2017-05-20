package oen.logorrhea.actors

import akka.actor.{ActorRef, Props, Terminated}
import akka.persistence.{PersistentActor, SnapshotOffer}
import oen.logorrhea.actors.RoomActor._
import oen.logorrhea.models.{Message, Messages}

class RoomActor(name: String) extends PersistentActor {

  var users: Set[ActorRef] = Set()
  var messages: Vector[Message] = Vector()
  val snapshotInterval = 1000

  override def persistenceId: String = s"room-$name"

  override def receiveRecover: Receive = {
    case Msg(msg) =>
      newMessage(msg)

    case SnapshotOffer(_, Msgs(msgs)) =>
      msgs.foreach(newMessage)
  }

  override def receiveCommand: Receive = {
    case Join =>
      users = users + sender()
      sender() ! Messages(messages)
      context.watch(sender())

    case Quit =>
      users = users - sender()

    case Terminated(actorRef) =>
      users = users - actorRef

    case msg: Message =>
      persist(Msg(msg))(m => newMessage(m.msg))

    case GetMessages =>
      sender() ! Messages(messages)
  }

  def newMessage(msg: Message) = {
    messages = messages :+ msg
    users.foreach(_ ! msg)
    if (lastSequenceNr % snapshotInterval == 0 && lastSequenceNr != 0) {
      if (messages.size > snapshotInterval) {
        messages = messages.drop(messages.size - snapshotInterval)
      }
      saveSnapshot(Msgs(messages))
    }
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

  sealed trait Evt
  case class Msg(msg: Message) extends Evt
  case class Msgs(messages: Vector[Message]) extends Evt
}
