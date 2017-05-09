package oen.logorrhea

import oen.logorrhea.components.ComponentsContainer
import oen.logorrhea.models.{Message, Username}
import org.scalajs.dom.html

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
          h4("Please enter yoru name"),
          components.usernameInput
        ),
        div(`class` := "modal-footer",
          components.connectButton
        )
      ),
      br,

      div(`class` := "row",
        div(`class` := "col s12 m4 l2 center",  // rooms
          div(`class` := "blue lighten-5",
            components.roomList
          )
        ),
        div(`class` := "col s12 m8 l8 center",  // msg
          div(`class` := "blue lighten-5 left-align",
            components.msgList
          )
        ),
        div(`class` := "col s12 m4 l2", // users
          div(`class` := "blue lighten-5",
            components.userList
          )
        )
      ),

      div(`class` := "row",
        div(`class` := "col s12 m2 l2 center", components.addRoomButton),
        div(`class` := "col s12 m1 l1 center", h3(`class` := "purple btn-large", components.usernameSpan)),
        div(`class` := "col s12 m5 l6 center", components.messageInput),
        div(`class` := "col s12 m4 l1 center", components.sendMessageButton)
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
      span(`class` := "blue lighten-4", message.msg)
    ).render
  }

  def refreshUserList(components: ComponentsContainer): Unit = {
    components.userList.innerHTML = ""

    components.mutable.users
      .toSeq.sorted((u1: Username, u2: Username) => JsUtils.localeCompare(u1.username, u2.username))
      .map(styleUserlistElement)
      .foreach(d => components.userList.appendChild(d))
  }

  protected def styleUserlistElement(username: Username): html.Div = {
    div(`class` := "row",
      div(`class` := "center-align btn blue", username.username)
    ).render
  }
}
