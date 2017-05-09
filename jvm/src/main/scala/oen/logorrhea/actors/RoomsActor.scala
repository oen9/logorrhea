package oen.logorrhea.actors

import akka.actor.{Actor, ActorRef, Props}
import oen.logorrhea.actors.RoomsActor._
import oen.logorrhea.models.Room

class RoomsActor extends Actor {

  val startRoom: RoomRef = RoomRef(context.actorOf(RoomActor.props(START_ROOM_NAME)), Room(START_ROOM_NAME))
  var rooms: Set[RoomRef] = Set(startRoom)

  override def receive: Receive = {
    case CreateRoom(name) =>
      val room = context.actorOf(RoomActor.props(name))
      rooms = rooms + RoomRef(room, Room(name))

    case GetRoom(roomName) =>
      rooms.find(roomRef => roomRef.room.name == roomName).foreach(sender() ! _)

    case GetStartRoom =>
      sender() ! startRoom
  }
}

object RoomsActor {
  val START_ROOM_NAME = "general"

  def props = Props(new RoomsActor)

  case class CreateRoom(name: String)
  case class GetRoom(name: String)
  case object GetStartRoom
  case class RemoveRoom(name: String)

  case class RoomRef(ref: ActorRef, room: Room)
}
