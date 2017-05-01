package oen.logorrhea.components

import org.scalajs.dom.html

import scalatags.JsDom.all._

case class ComponentsContainer(
  usernameInput: html.Input = input(`type` := "text", placeholder := "Your username").render,

  aboutButton: html.Span = span("about").render,
  signOutButton: html.Span = span("Sign out").render,

  roomList: html.Div = div().render,
  msgList: html.Div = div().render,
  userList: html.Div = div().render,

  addRoomButton: html.Anchor = a(`class` := "btn-floating btn waves-effect waver-light", i(`class` := "material-icons", "add")).render,
  messageInput: html.Input = input(`type` := "text", placeholder := "Type here....").render,
  sendMessageButton: html.Span = span(`class` := "btn waves-effect waves-light", i(`class` := "material-icons", "send")).render
)
