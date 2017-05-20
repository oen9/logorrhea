package oen.logorrhea.actors

import akka.actor.{ActorLogging, ActorRef, PoisonPill, Props}
import akka.persistence.{PersistentActor, SnapshotOffer}
import oen.logorrhea.SharedStrings
import oen.logorrhea.actors.RoomsActor._
import oen.logorrhea.models._

class RoomsActor extends PersistentActor with ActorLogging {

  val startRoom: RoomRef = RoomRef(context.actorOf(RoomActor.props(SharedStrings.START_ROOM_NAME)), Room(SharedStrings.START_ROOM_NAME))
  var rooms: Set[RoomRef] = Set(startRoom)

  val snapshotInterval = 1000

  override def persistenceId: String = "rooms-dispatcher"

  override def receiveRecover: Receive = {
    case RoomAdded(name) =>
      createRoom(name)

    case RoomRemoved(name) =>
      removeRoom(name)

    case SnapshotOffer(_, RoomsState(roomList)) =>
      roomList.foreach(createRoom)
  }

  override def receiveCommand: Receive = {

    case CreateRoom(name) =>
      if (rooms.exists(_.room.name.equalsIgnoreCase(name))) {
        sender() ! RoomRejected

      } else {
        sender() ! RoomAccepted(name)
        persist(RoomAdded(name))(_ => createRoom(name))
      }

    case GetRoom(roomName) =>
      rooms.find(roomRef => roomRef.room.name == roomName).foreach(sender() ! _)

    case GetStartRoom =>
      sender() ! startRoom
      sender() ! RoomList(rooms.map(_.room))

    case RemoveRoom(name) =>
      if (!name.equalsIgnoreCase(SharedStrings.START_ROOM_NAME)) {
        rooms.find(_.room.name.equalsIgnoreCase(name))
          .foreach(_ => {
            persist(RoomRemoved(name))(_ => removeRoom(name))
          })
      }
  }

  def createRoom(name: String) = {
    val roomActor = context.actorOf(RoomActor.props(name))
    val roomRef = RoomRef(roomActor, Room(name))
    rooms = rooms + roomRef
    context.system.eventStream.publish(RoomCreated(roomRef.room))

    if (lastSequenceNr % snapshotInterval == 0 && lastSequenceNr != 0)
      saveSnapshot(RoomsState(rooms.map(_.room.name).filter(_ != SharedStrings.START_ROOM_NAME)))
  }

  def removeRoom(name: String) = {
    rooms.find(_.room.name.equalsIgnoreCase(name))
      .foreach(roomRef => {
        roomRef.ref ! PoisonPill
        rooms = rooms - roomRef
        context.system.eventStream.publish(RoomDeleted(roomRef.room))
      })
  }
}

object RoomsActor {

  def props = Props(new RoomsActor)

  case class GetRoom(name: String)
  case object GetStartRoom
  case class RemoveRoom(name: String)

  case class RoomRef(ref: ActorRef, room: Room)

  sealed trait Evt
  case class RoomAdded(name: String) extends Evt
  case class RoomRemoved(name: String) extends Evt
  case class RoomsState(rooms: Set[String]) extends Evt
}
