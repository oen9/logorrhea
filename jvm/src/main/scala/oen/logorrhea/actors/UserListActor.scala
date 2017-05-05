package oen.logorrhea.actors

import akka.actor.{Actor, ActorRef, Props, Terminated}
import oen.logorrhea.actors.UserListActor.GetUsers
import oen.logorrhea.models.{UserAdded, UserList, UserRemoved, Username}

class UserListActor extends Actor {

  var users: Set[(ActorRef, Username)] = Set[(ActorRef, Username)] ()

  override def receive: Receive = {
    case  added: UserAdded =>
      context.system.eventStream.publish(added)
      users = users + ((sender(), added.user))

      context.watch(sender())

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

  case object GetUsers
}
