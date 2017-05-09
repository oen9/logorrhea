package oen.logorrhea.actors

import akka.actor.{Actor, ActorRef, Props}
import oen.logorrhea.actors.RoomsActor._
import oen.logorrhea.models.{CreateRoom, Room, RoomCreated, RoomList}

class RoomsActor extends Actor {

  val startRoom: RoomRef = RoomRef(context.actorOf(RoomActor.props(START_ROOM_NAME)), Room(START_ROOM_NAME))
  var rooms: Set[RoomRef] = Set(startRoom)

  override def receive: Receive = {

    case CreateRoom(name) =>
      val roomActor = context.actorOf(RoomActor.props(name))
      val roomRef = RoomRef(roomActor, Room(name))
      rooms = rooms + roomRef
      context.system.eventStream.publish(RoomCreated(roomRef.room))

    case GetRoom(roomName) =>
      rooms.find(roomRef => roomRef.room.name == roomName).foreach(sender() ! _)

    case GetStartRoom =>
      sender() ! startRoom
      sender() ! RoomList(rooms.map(_.room))
  }
}

object RoomsActor {
  val START_ROOM_NAME = "general"

  def props = Props(new RoomsActor)

  case class GetRoom(name: String)
  case object GetStartRoom
  case class RemoveRoom(name: String)

  case class RoomRef(ref: ActorRef, room: Room)
}
