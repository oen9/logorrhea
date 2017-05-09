package oen.logorrhea.models

import derive.key

sealed trait Data

@key("username") case class Username(username: String) extends Data

@key("message") case class Message(msg: String, room: String = "global", from: Option[String] = None) extends Data
@key("messages") case class Messages(messages: Vector[Message]) extends Data

@key("use-list") case class UserList(users: Set[Username]) extends Data
@key("user-added") case class UserAdded(user: Username) extends Data
@key("user-removed") case class UserRemoved(user: Username) extends Data

@key("room") case class Room(name: String) extends Data
@key("room-list") case class RoomList(rooms: Set[Room])
@key("room-created") case class RoomCreated(name: String) extends Data

@key("ping") case object Ping extends Data

object Data {
  def toJson(data: Data): String = {
    upickle.default.write(data)
  }

  def fromJson(json: String): Data = {
    upickle.default.read[Data](json)
  }
}
