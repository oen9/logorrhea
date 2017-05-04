package oen.logorrhea.components

import oen.logorrhea.HtmlContent
import oen.logorrhea.materialize.Materialize
import oen.logorrhea.models.Data._
import oen.logorrhea.models.{Message, Ping, Username}
import org.scalajs.dom
import org.scalajs.dom._

object WebsockConnector {

  def connect(components: ComponentsContainer): Unit = {
    Materialize.Materialize.toast("Connecting...", 4000)

    close(components)

    val protocol = if ("http:" == dom.window.location.protocol) "ws://" else "wss://"
    val uri = protocol + dom.window.location.host + "/websock"
    val socket = new dom.WebSocket(uri)

    components.mutable.webSocket = Some(socket)

    socket.onopen = (_: Event) => {
      Materialize.Materialize.toast("Connected!", 4000)
      components.mutable.username.map(u => toJson(Username(u))).foreach(socket.send)
      components.mutable.pingIntervalId = Some(dom.window.setInterval(() => { socket.send(toJson(Ping)) }, 30000))
    }

    socket.onerror = (e: dom.ErrorEvent) => {
      Materialize.Materialize.toast("Connection error. Disconnected. Error: " + e.message, 4000)
    }

    socket.onclose = (_: CloseEvent) => {
      Materialize.Materialize.toast("Connection closed.", 4000)
      Materialize.Materialize.toast("Trying to reconnect in 5 seconds", 4000)
      dom.window.setTimeout(() => connect(components), 5000)
    }

    socket.onmessage = (e: dom.MessageEvent) => {
      fromJson(e.data.toString) match {
        case msg: Message => newMessage(msg, components)
        case unknown => println("unknown message:" + unknown)
      }
    }
  }

  def send(msg: String, components: ComponentsContainer): Unit = {
    components.mutable.webSocket.foreach(_.send(toJson(Message(msg))))
  }

  def close(components: ComponentsContainer): Unit = {
    components.mutable.webSocket.foreach(_.close())
    components.mutable.pingIntervalId.foreach(dom.window.clearInterval)
  }

  protected def newMessage(message: Message, components: ComponentsContainer): Unit = {
    val prettyMessage = HtmlContent.createMsg(message, components.mutable.username.get)

    components.msgList.appendChild(prettyMessage)
    components.msgList.scrollTop = components.msgList.scrollHeight
  }
}
