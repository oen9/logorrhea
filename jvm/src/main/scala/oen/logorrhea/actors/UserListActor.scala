package oen.logorrhea.actors

import akka.actor.{Actor, ActorRef, Props, Terminated}
import oen.logorrhea.actors.UserListActor.{GetUsers, UserAccepted}
import oen.logorrhea.models._

class UserListActor extends Actor {

  var users: Set[(ActorRef, Username)] = Set[(ActorRef, Username)] ()

  override def receive: Receive = {
    case added: UserAdded =>
      if (users.exists(u => u._2.username.equalsIgnoreCase(added.user.username))) {
        sender() ! UserRejected

      } else {
        context.system.eventStream.publish(added)
        users = users + ((sender(), added.user))

        context.watch(sender())

        sender() ! UserAccepted(added.user)
      }

    case removed: UserRemoved  =>
      context.system.eventStream.publish(removed)
      users = users - ((sender(), removed.user))

    case GetUsers =>
      sender() ! UserList(users.map { case (_, u) => u })

    case Terminated(actorRef) =>
      users.filter((u) => actorRef == u._1).foreach(u => {
        users = users - u
        context.system.eventStream.publish(UserRemoved(u._2))
      })
  }
}

object UserListActor {
  def props = Props(new UserListActor)
  val name: String = "user-list"

  case class UserAccepted(username: Username)
  case object GetUsers
}
