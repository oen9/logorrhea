package oen.logorrhea.components

import oen.logorrhea.materialize.JQueryHelper
import oen.logorrhea.models.DeleteRoom
import org.scalajs.dom.{KeyboardEvent, MouseEvent}

object ComponentsLogic {

  def initComponentsLogic(components: ComponentsContainer): Unit = {
    UsernamePicker.initPicker(components)

    components.addRoomButton.onclick = (_: MouseEvent) => {
      JQueryHelper.openNewRoomModal()
      components.newRoomInput.focus()
    }
    components.newRoomAccept.onclick = (_: MouseEvent) => newRoom(components)
    components.newRoomInput.onkeydown = (e: KeyboardEvent) => if ("Enter" == e.key) newRoom(components)
    components.deleteRoomAccept.onclick = (_: MouseEvent) => deleteRoom(components)

    components.sendMessageButton.onclick = (_: MouseEvent) => sendMsg(components)
    components.messageInput.onkeydown = (e: KeyboardEvent) => if ("Enter" == e.key) sendMsg(components)
  }

  protected def newRoom(components: ComponentsContainer): Unit = {
    val newRoomName = components.newRoomInput.value
    if (!newRoomName .isEmpty) {
      WebsockConnector.createRoom(newRoomName, components)
    }
  }

  protected def deleteRoom(components: ComponentsContainer): Unit = {
    val roomName = components.deleteRoomName.innerHTML
    WebsockConnector.send(DeleteRoom(roomName), components)
    JQueryHelper.closeDeleteRoomModal
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
