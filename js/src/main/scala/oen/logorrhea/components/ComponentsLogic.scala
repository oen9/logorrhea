package oen.logorrhea.components

import org.scalajs.dom.{KeyboardEvent, MouseEvent}

object ComponentsLogic {

  def initComponentsLogic(components: ComponentsContainer): Unit = {
    UsernamePicker.initPicker(components)

    components.sendMessageButton.onclick = (_: MouseEvent) => sendMsg(components)
    components.messageInput.onkeydown = (e: KeyboardEvent) => if ("Enter" == e.key) sendMsg(components)
  }

  protected def sendMsg(components: ComponentsContainer): Unit = {
    val newText = components.messageInput.value
    if (!newText.isEmpty) {
      WebsockConnector.send(newText, components)
      components.messageInput.value = ""
    }

    components.messageInput.focus()
  }
}
