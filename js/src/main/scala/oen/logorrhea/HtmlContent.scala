package oen.logorrhea

import oen.logorrhea.components.ComponentsContainer
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
          a(`class` := "modal-action modal-close waves-effect waves-green btn-flat", href := "#!" , "Connect")
        )
      ),
      br,

      div(`class` := "row",
        div(`class` := "col s12 m4 l2 center",  // rooms
          div(`class` := "blue lighten-5 scrollable-content",
            components.roomList
          )
        ),
        div(`class` := "col s12 m8 l8 center",  // msg
          div(`class` := "blue lighten-5 scrollable-content left-align",
            components.msgList
          )
        ),
        div(`class` := "col s12 m4 l2", // users
          div(`class` := "blue lighten-5 scrollable-content",
            components.userList
          )
        )
      ),

      div(`class` := "row",
        div(`class` := "col s12 m2 l2 center", components.addRoomButton),
        div(`class` := "col s12 m5 l7 center", components.messageInput),
        div(`class` := "col s12 m5 l1 center", components.sendMessageButton)
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
}
