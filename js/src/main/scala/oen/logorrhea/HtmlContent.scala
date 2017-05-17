package oen.logorrhea

import oen.logorrhea.components.{ComponentsContainer, WebsockConnector}
import oen.logorrhea.models.{Message, Room, Username}
import org.scalajs.dom.{MouseEvent, html}

import scalatags.JsDom.all._
import scalatags.JsDom.tags2

object HtmlContent {
  def initHeader(components: ComponentsContainer): html.Element = {
    tags2.nav(
      ul(`class` := "dropdown-content", id := "settings-dropdown",
        li(components.aboutButton),
        li(`class` := "divider"),
        li(components.signOutButton)
      ),

      div(`class` := "nav-wrapper",
        a(`class` := "brand-logo", href := "#", "logorrhea"),
        ul(`class` := "right",
          li(
            a(`class` := "dropdown-button", href := "#", attr("data-activates") := "settings-dropdown",
              i(`class` := "material-icons right", "settings")
            )
          )
        )
      )
    ).render
  }

  def initMain(components: ComponentsContainer): html.Div = {
    div(
      div(`class` := "modal", id := "user-name-modal",
        div(`class` := "modal-content",
          h4("Please enter your name"),
          components.newRoomNotification,
          components.usernameInput
        ),
        div(`class` := "modal-footer",
          components.connectButton
        )
      ),
      div(`class` := "modal", id := "new-room-modal",
        div(`class` := "modal-content",
          h4("Please enter new room name"),
          components.newRoomInput
        ),
        div(`class` := "modal-footer",
          components.newRoomAccept,
          span(`class` := "modal-action waves-effect waves-green btn-flat modal-close red", "cancel").render
        )
      ),
      br,

      div(`class` := "row",
        div(`class` := "col s2 m2 l2 center",  // rooms
          div(`class` := "blue lighten-5",
            components.roomList
          )
        ),
        div(`class` := "col s8 m8 l8 center",  // msg
          div(`class` := "blue lighten-5 left-align",
            components.msgList
          )
        ),
        div(`class` := "col s2 m2 l2", // users
          div(`class` := "blue lighten-5",
            components.userList
          )
        )
      ),

      EmoteProcessor.genEmoteList(components),
      div(`class` := "row",
        div(`class` := "col s2 m2 l2 center", components.addRoomButton),
        div(`class` := "col s7 m7 l7 center", components.messageInput),
        div(`class` := "col s1 m1 l1 center",
          components.sendMessageButton,
          a(`class` := "emote-dropdown", href := "#", attr("data-activates") := "emote-dropdown", i(`class` := "material-icons right", "dialpad"))
        )
      )

    ).render
  }

  def initFooter(): html.Div = {
    div(`class` := "footer-copyright",
      div(`class` := "container",
        "© 2017 oen",
        a(`class` := "grey-text text-lighten-4 right", target := "_blank", href := "https://github.com/oen9/logorrhea", "github")
      )
    ).render
  }

  def createMsg(message: Message, username: String): html.Div = {
    val from = message.from.get
    val style = if (from == username) "pink-text" else "green-text"

    div(
      span(`class` := style, message.from.get, span(": ")),
      span(`class` := "blue lighten-4", EmoteProcessor.process(message.msg))
    ).render
  }

  def refreshUserList(components: ComponentsContainer): Unit = {
    components.userList.innerHTML = ""

    components.mutable.users
      .toSeq.sorted((u1: Username, u2: Username) => JsUtils.localeCompare(u1.username, u2.username))
      .map(u => createUserListElement(u, components))
      .foreach(d => components.userList.appendChild(d))
  }

  protected def createUserListElement(username: Username, components: ComponentsContainer): html.Div = {
    val color = components.mutable.username.filter(_ == username.username).map(_ => "green").getOrElse("indigo")

    div(`class` := "row",
      div(`class` := s"center-align btn $color", username.username)
    ).render
  }

  def refreshRoomList(components: ComponentsContainer): Unit = {
    components.roomList.innerHTML = ""

    components.mutable.rooms
      .toSeq.sorted((r1: Room, r2: Room) => JsUtils.localeCompare(r1.name, r2.name))
      .map(r => createRoomListElement(r, components))
      .foreach(r => components.roomList.appendChild(r))
  }

  protected def createRoomListElement(room: Room, components: ComponentsContainer): html.Div = {
    val color = components.mutable.currentRoom.filter(_.name == room.name).map(_ => "green").getOrElse("indigo")
    val button = div(`class` := s"center-align btn $color waves-effect waves-light", room.name).render

    button.onclick = (_: MouseEvent) => { WebsockConnector.send(room, components) }

    div(`class` := "row",
      button
    ).render
  }

  def clearNotifications(components: ComponentsContainer): Unit = {
    components.newRoomNotification.innerHTML = ""
  }

  def activeUserRejectedNotification(components: ComponentsContainer): Unit = {
    components.newRoomNotification.innerHTML = ""
    components.newRoomNotification.appendChild(span("Username in use. Please choose again").render)
  }
}
