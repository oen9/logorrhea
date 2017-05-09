package oen.logorrhea.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import oen.logorrhea.actors.RoomActor.{Join, Quit}
import oen.logorrhea.actors.RoomsActor.{GetRoom, GetStartRoom, RoomRef}
import oen.logorrhea.actors.UserActor.WebsockOutput
import oen.logorrhea.models._

class UserActor(userListActor: ActorRef, roomsActor: ActorRef) extends Actor with ActorLogging {

  var currentRoom: Option[RoomRef] = None

  override def receive: Receive = emptyActor

  def emptyActor: Receive = {
    case WebsockOutput(out) => context.become(waitingForUsername(out))
  }

  def waitingForUsername(out: ActorRef): Receive = {
    case u: Username =>
      context.become(handlingMessages(out, u.username))

      context.system.eventStream.subscribe(self, classOf[UserAdded])
      context.system.eventStream.subscribe(self, classOf[UserRemoved])
      context.system.eventStream.subscribe(self, classOf[RoomCreated])

      userListActor ! UserListActor.GetUsers
      userListActor ! UserAdded(u)
      roomsActor ! GetStartRoom
  }

  def handlingMessages(out: ActorRef, username: String): Receive = {
    case inMsg @ Message(_, _, None) =>
      currentRoom.foreach(_.ref ! inMsg.copy(from = Some(username)))

    case roomRef: RoomRef =>
      roomRef.ref ! Join
      currentRoom = Some(roomRef)
      out ! roomRef.room

    case createRoom: CreateRoom =>
      roomsActor ! createRoom

    case Room(name) =>
      currentRoom.foreach(_.ref ! Quit)
      roomsActor ! GetRoom(name)

    case Ping =>

    case toForward => out ! toForward
  }
}

object UserActor {
  def props(userListActor: ActorRef, roomsActor: ActorRef) = Props(new UserActor(userListActor, roomsActor))

  case class WebsockOutput(out: ActorRef)
}
