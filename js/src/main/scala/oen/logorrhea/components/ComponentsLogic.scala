package oen.logorrhea.components

import oen.logorrhea.materialize.Materialize
import org.scalajs.dom.{KeyboardEvent, MouseEvent, html}

import scalatags.JsDom.all._

object ComponentsLogic {

  def initComponentsLogic(components: ComponentsContainer): Unit = {
    components.messageInput.onkeydown = (e: KeyboardEvent) => {
      if ("Enter" == e.key) {
        sendMsg(components.messageInput, components.msgList)
      }
    }

    components.sendMessageButton.onclick = (e: MouseEvent) => sendMsg(components.messageInput, components.msgList)

    components.msgList.scrollTop = components.msgList.scrollHeight
  }

  def sendMsg(text: html.Input, target: html.Div): Unit = {
    val newText = text.value

    if (!newText.isEmpty) {
      Materialize.Materialize.toast(newText, 4000)

      target.appendChild(div(newText).render)

      target.scrollTop = target.scrollHeight
      text.value = ""
    }

    text.focus()
  }
}
