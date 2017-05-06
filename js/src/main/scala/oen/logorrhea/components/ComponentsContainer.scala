package oen.logorrhea.components

import oen.logorrhea.models.Username
import org.scalajs.dom.{WebSocket, html}

import scalatags.JsDom.all._

case class ComponentsContainer(
  usernameInput: html.Input = input(`type` := "text", placeholder := "Your username").render,
  connectButton: html.Span = span(`class` := "modal-action waves-effect waves-green btn-flat", "connect").render,

  aboutButton: html.Span = span("about").render,
  signOutButton: html.Span = span("Sign out").render,

  roomList: html.Div = div(`class` := "scrollable-content").render,
  msgList: html.Div = div(`class` := "scrollable-content").render,
  userList: html.Div = div(`class` := "scrollable-content center").render,

  addRoomButton: html.Anchor = a(`class` := "btn-floating btn waves-effect waver-light", i(`class` := "material-icons", "add")).render,
  usernameSpan: html.Span = span().render,
  messageInput: html.Input = input(`type` := "text", placeholder := "Type here....").render,
  sendMessageButton: html.Span = span(`class` := "btn waves-effect waves-light", i(`class` := "material-icons", "send")).render,

  mutable: MutableContainer = new MutableContainer()
)

class MutableContainer(
  var username: Option[String] = None,
  var webSocket: Option[WebSocket] = None,
  var pingIntervalId: Option[Int] = None,
  var users: Set[Username] = Set()
)
