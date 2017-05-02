package oen.logorrhea.components

import org.scalajs.dom.html

import scalatags.JsDom.all._

case class ComponentsContainer(
  usernameInput: html.Input = input(`type` := "text", placeholder := "Your username").render,
  connectButton: html.Span = span(`class` := "modal-action waves-effect waves-green btn-flat", "connect").render,

  aboutButton: html.Span = span("about").render,
  signOutButton: html.Span = span("Sign out").render,

  roomList: html.Div = div(`class` := "scrollable-content").render,
  msgList: html.Div = div(`class` := "scrollable-content").render,
  userList: html.Div = div(`class` := "scrollable-content").render,

  addRoomButton: html.Anchor = a(`class` := "btn-floating btn waves-effect waver-light", i(`class` := "material-icons", "add")).render,
  usernameSpan: html.Span = span().render,
  messageInput: html.Input = input(`type` := "text", placeholder := "Type here....").render,
  sendMessageButton: html.Span = span(`class` := "btn waves-effect waves-light", i(`class` := "material-icons", "send")).render
)
