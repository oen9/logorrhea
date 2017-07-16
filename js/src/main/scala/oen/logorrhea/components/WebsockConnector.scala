package oen.logorrhea.components

import oen.logorrhea.HtmlContent
import oen.logorrhea.materialize.{JQueryHelper, Materialize}
import oen.logorrhea.models.Data._
import oen.logorrhea.models._
import org.scalajs.dom
import org.scalajs.dom._

class WebsockConnector(htmlContent: HtmlContent, components: ComponentsContainer, usernamePicker: UsernamePicker) {

  def connect(): Unit = {
    Materialize.Materialize.toast("Connecting...", 2000)

    close()

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
      dom.window.setTimeout(() => connect(), 5000)
    }

    socket.onmessage = (e: dom.MessageEvent) => {
      fromJson(e.data.toString) match {
        case msg: Message => newMessage(msg)
        case msgs: Messages => fillMessages(msgs)

        case userList: UserList =>
          initUsernameAccepted(components)
          handleUserList(userList)
        case added: UserAdded => handleUserAdded(added)
        case removed: UserRemoved => handleUserRemoved(removed)
        case UserRejected => handleUserRejected(components)

        case roomList: RoomList => handleRooms(roomList)
        case roomCreated: RoomCreated => handleRoomCreated(roomCreated)
        case roomDeleted: RoomDeleted => handleRoomDeleted(roomDeleted)
        case room: Room => components.mutable.currentRoom = Some(room)
        case RoomRejected => handleRoomRejected(components)
        case _: RoomAccepted => handleRoomAccepted(components)

        case unknown => println("unknown message:" + unknown)
      }
    }
  }

  def send(msg: Data): Unit = {
    components.mutable.webSocket.foreach(_.send(toJson(msg)))
  }

  def send(msg: String): Unit = {
    for {
      ws <- components.mutable.webSocket
      room <- components.mutable.currentRoom
    } ws.send(toJson(Message(msg, room.name)))
  }

  def createRoom(roomName: String): Unit = {
    components.mutable.webSocket.foreach(_.send(toJson(CreateRoom(roomName))))
  }

  def close(): Unit = {
    components.mutable.webSocket.foreach(ws => {
      ws.onclose = (e: CloseEvent) => {}
      ws.close()
    })
    components.mutable.pingIntervalId.foreach(dom.window.clearInterval)
    components.mutable.pingIntervalId = None
  }

  protected def fillMessages(messages: Messages): Unit = {
    components.msgList.innerHTML = ""
    messages.messages.foreach(newMessage)
    htmlContent.refreshRoomList(send)
    components.messageInput.focus()
  }

  protected def newMessage(message: Message): Unit = {
    val prettyMessage = htmlContent.createMsg(message, components.mutable.username.get)

    components.msgList.appendChild(prettyMessage)
    components.msgList.scrollTop = components.msgList.scrollHeight
    JQueryHelper.refreshTooltips()
  }

  protected def initUsernameAccepted(components: ComponentsContainer): Unit = {
    htmlContent.clearNotifications()
    JQueryHelper.closeUsernameModal()
    components.messageInput.focus()
  }

  protected def handleUserList(userList: UserList): Unit = {
    components.mutable.users = userList.users
    htmlContent.refreshUserList()
  }

  protected def handleUserAdded(added: UserAdded): Unit = {
    components.mutable.users = components.mutable.users + added.user
    htmlContent.refreshUserList()
  }

  protected def handleUserRemoved(removed: UserRemoved): Unit = {
    components.mutable.users = components.mutable.users - removed.user
    htmlContent.refreshUserList()
  }

  protected def handleRooms(roomList: RoomList): Unit = {
    components.mutable.rooms = roomList.rooms
    htmlContent.refreshRoomList(send)
  }

  protected def handleRoomCreated(roomCreated: RoomCreated): Unit = {
    components.mutable.rooms = components.mutable.rooms + roomCreated.room
    htmlContent.refreshRoomList(send)
  }

  protected def handleRoomDeleted(roomDeleted: RoomDeleted): Unit = {
    components.mutable.rooms = components.mutable.rooms - roomDeleted.room
    htmlContent.refreshRoomList(send)
    components.messageInput.focus()
  }

  protected def handleUserRejected(components: ComponentsContainer): Unit = {
    htmlContent.activeUserRejectedNotification()
    usernamePicker.signOut(close)
  }

  protected def handleRoomRejected(components: ComponentsContainer): Unit = {
    htmlContent.newRoomRejectedNotification()
  }

  protected def handleRoomAccepted(components: ComponentsContainer): Unit = {
    htmlContent.clearNotifications()
    JQueryHelper.closeNewRoomModal()
    components.messageInput.focus()
    components.newRoomInput.value = ""
  }
}
