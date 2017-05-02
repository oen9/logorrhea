package oen.logorrhea.components

import oen.logorrhea.materialize.Materialize
import org.scalajs.dom.{KeyboardEvent, MouseEvent}

import scalatags.JsDom.all._

object ComponentsLogic {

  final val USERNAME_KEY = "username"

  def initComponentsLogic(components: ComponentsContainer): Unit = {
    components.messageInput.onkeydown = (e: KeyboardEvent) => if ("Enter" == e.key) sendMsg(components)
    components.sendMessageButton.onclick = (_: MouseEvent) => sendMsg(components)

    UsernamePicker.initPicker(components)
  }

  def sendMsg(components: ComponentsContainer): Unit = {
    val newText = components.messageInput.value

    if (!newText.isEmpty) {
      Materialize.Materialize.toast(newText, 4000)

      val prettyMessage = div(
        span(`class` := "pink-text",
          components.usernameSpan.innerHTML,
          span(": ")
        ),
        span(`class` := "blue lighten-4", newText)
      ).render

      components.msgList.appendChild(prettyMessage)

      println(components.msgList.scrollTop)
      println(components.msgList.scrollHeight)
      components.msgList.scrollTop = components.msgList.scrollHeight

      components.messageInput.value = ""
    }

    components.messageInput.focus()
  }
}
