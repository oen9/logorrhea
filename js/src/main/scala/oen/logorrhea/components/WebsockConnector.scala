package oen.logorrhea.components

import oen.logorrhea.HtmlContent
import oen.logorrhea.materialize.{JQueryHelper, Materialize}
import oen.logorrhea.models.Data._
import oen.logorrhea.models._
import org.scalajs.dom
import org.scalajs.dom._

object WebsockConnector {

  def connect(components: ComponentsContainer): Unit = {
    Materialize.Materialize.toast("Connecting...", 2000)

    close(components)

    val protocol = if ("http:" == dom.window.location.protocol) "ws://" else "wss://"
    val uri = protocol + dom.window.location.host + "/websock"
    val socket = new dom.WebSocket(uri)

    components.mutable.webSocket = Some(socket)

    socket.onopen = (_: Event) => {
      Materialize.Materialize.toast("Connected!", 2000)
      components.mutable.username.map(u => toJson(Username(u))).foreach(socket.send)
      components.mutable.pingIntervalId = Some(dom.window.setInterval(() => { socket.send(toJson(Ping)) }, 30000))
    }

    socket.onerror = (e: dom.ErrorEvent) => {
      Materialize.Materialize.toast("Connection error. Disconnected. Error: " + e.message, 2000)
    }

    socket.onclose = (_: CloseEvent) => {
      Materialize.Materialize.toast("Connection closed.", 2000)
      Materialize.Materialize.toast("Trying to reconnect in 5 seconds", 2000)
      dom.window.setTimeout(() => connect(components), 5000)
    }

    socket.onmessage = (e: dom.MessageEvent) => {
      fromJson(e.data.toString) match {
        case msg: Message => newMessage(msg, components)
        case msgs: Messages => fillMessages(msgs, components)

        case userList: UserList =>
          initUsernameAccepted(components)
          handleUserList(userList, components)
        case added: UserAdded => handleUserAdded(added, components)
        case removed: UserRemoved => handleUserRemoved(removed, components)
        case UserRejected => handleUserRejected(components)

        case roomList: RoomList => handleRooms(roomList, components)
        case roomCreated: RoomCreated => handleRoomCreated(roomCreated, components)
        case room: Room => components.mutable.currentRoom = Some(room)

        case unknown => println("unknown message:" + unknown)
      }
    }
  }

  def send(msg: Data, components: ComponentsContainer): Unit = {
    components.mutable.webSocket.foreach(_.send(toJson(msg)))
  }

  def send(msg: String, components: ComponentsContainer): Unit = {
    for {
      ws <- components.mutable.webSocket
      room <- components.mutable.currentRoom
    } ws.send(toJson(Message(msg, room.name)))
  }

  def createRoom(roomName: String, components: ComponentsContainer): Unit = {
    components.mutable.webSocket.foreach(_.send(toJson(CreateRoom(roomName))))
  }

  def close(components: ComponentsContainer): Unit = {
    components.mutable.webSocket.foreach(ws => {
      ws.onclose = (e: CloseEvent) => {}
      ws.close()
    })
    components.mutable.pingIntervalId.foreach(dom.window.clearInterval)
    components.mutable.pingIntervalId = None
  }

  protected def fillMessages(messages: Messages, components: ComponentsContainer): Unit = {
    components.msgList.innerHTML = ""
    messages.messages.foreach(newMessage(_, components))
    HtmlContent.refreshRoomList(components)
  }

  protected def newMessage(message: Message, components: ComponentsContainer): Unit = {
    val prettyMessage = HtmlContent.createMsg(message, components.mutable.username.get)

    components.msgList.appendChild(prettyMessage)
    components.msgList.scrollTop = components.msgList.scrollHeight
  }

  protected def initUsernameAccepted(components: ComponentsContainer): Unit = {
    HtmlContent.clearNotifications(components)
    JQueryHelper.closeUsernameModal()
    components.messageInput.focus()
  }

  protected def handleUserList(userList: UserList, components: ComponentsContainer): Unit = {
    components.mutable.users = userList.users
    HtmlContent.refreshUserList(components)
  }

  protected def handleUserAdded(added: UserAdded, components: ComponentsContainer): Unit = {
    components.mutable.users = components.mutable.users + added.user
    HtmlContent.refreshUserList(components)
  }

  protected def handleUserRemoved(removed: UserRemoved, components: ComponentsContainer): Unit = {
    components.mutable.users = components.mutable.users - removed.user
    HtmlContent.refreshUserList(components)
  }

  protected def handleRooms(roomList: RoomList, components: ComponentsContainer): Unit = {
    components.mutable.rooms = roomList.rooms
    HtmlContent.refreshRoomList(components)
  }

  protected def handleRoomCreated(roomCreated: RoomCreated, components: ComponentsContainer): Unit = {
    components.mutable.rooms = components.mutable.rooms + roomCreated.room
    HtmlContent.refreshRoomList(components)
  }

  protected def handleUserRejected(components: ComponentsContainer): Unit = {
    HtmlContent.activeUserRejectedNotification(components)
    UsernamePicker.signOut(components)
  }
}
