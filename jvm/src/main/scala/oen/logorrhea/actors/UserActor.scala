package oen.logorrhea.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import oen.logorrhea.actors.RoomActor.{Join, Quit}
import oen.logorrhea.actors.RoomsActor.{GetRoom, GetStartRoom, RemoveRoom, RoomRef}
import oen.logorrhea.actors.UserActor.WebsockOutput
import oen.logorrhea.actors.UserListActor.UserAccepted
import oen.logorrhea.models._

class UserActor(userListActor: ActorRef, roomsActor: ActorRef) extends Actor with ActorLogging {

  var currentRoom: Option[RoomRef] = None

  override def receive: Receive = emptyActor

  def emptyActor: Receive = {
    case WebsockOutput(out) => context.become(waitingForUsername(out))
  }

  def waitingForUsername(out: ActorRef): Receive = {
    case u: Username =>
      userListActor ! UserAdded(u)

    case UserRejected =>
      out ! UserRejected

    case UserAccepted(u) =>
      context.become(handlingMessages(out, u.username))

      context.system.eventStream.subscribe(self, classOf[UserAdded])
      context.system.eventStream.subscribe(self, classOf[UserRemoved])
      context.system.eventStream.subscribe(self, classOf[RoomCreated])
      context.system.eventStream.subscribe(self, classOf[RoomDeleted])

      userListActor ! UserListActor.GetUsers
      roomsActor ! GetStartRoom
  }

  def handlingMessages(out: ActorRef, username: String): Receive = {
    case inMsg @ Message(_, _, None) =>
      currentRoom.foreach(_.ref ! inMsg.copy(from = Some(username)))

    case roomRef: RoomRef =>
      currentRoom.foreach(rr => context.unwatch(rr.ref))
      context.watch(roomRef.ref)
      currentRoom = Some(roomRef)

      roomRef.ref ! Join
      out ! roomRef.room

    case createRoom: CreateRoom =>
      roomsActor ! createRoom

    case Room(name) =>
      currentRoom.foreach(_.ref ! Quit)
      roomsActor ! GetRoom(name)

    case DeleteRoom(name) =>
      roomsActor ! RemoveRoom(name)

    case Ping =>

    case toForward: Data => out ! toForward

    case Terminated(actorRef) =>
      currentRoom.filter(_.ref == actorRef).foreach(_ => currentRoom = None)
  }
}

object UserActor {
  def props(userListActor: ActorRef, roomsActor: ActorRef) = Props(new UserActor(userListActor, roomsActor))

  case class WebsockOutput(out: ActorRef)
}
