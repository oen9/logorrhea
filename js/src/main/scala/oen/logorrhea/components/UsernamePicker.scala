package oen.logorrhea.components

import oen.logorrhea.components.ComponentsLogic.USERNAME_KEY
import oen.logorrhea.materialize.JQueryHelper
import org.scalajs.dom
import org.scalajs.dom.{KeyboardEvent, MouseEvent}

object UsernamePicker {

  def initPicker(components: ComponentsContainer): Unit = {
    components.connectButton.onclick = (_: MouseEvent) => handleNewUsername(components)
    components.usernameInput.onkeydown = (e: KeyboardEvent) => if ("Enter" == e.key) handleNewUsername(components)

    components.signOutButton.onclick = (_: MouseEvent) => {
      dom.window.localStorage.removeItem(USERNAME_KEY)
      components.usernameSpan.innerHTML = ""
      openModalUsernamePicker(components)
    }

    restoreUsername(components)
    handleUsernameNotFound(components)
  }

  protected def restoreUsername(components: ComponentsContainer): Unit = {
    Option(dom.window.localStorage.getItem(USERNAME_KEY)).foreach(username => {
      indicateUsername(components, username)
    })
  }

  protected def indicateUsername(components: ComponentsContainer, username: String): Unit = {
    components.usernameSpan.innerHTML = username
    components.messageInput.focus()
  }

  protected def handleNewUsername(components: ComponentsContainer): Unit = {
    val newUsername = components.usernameInput.value
    if (!newUsername.isEmpty) {
      dom.window.localStorage.setItem(USERNAME_KEY, newUsername)
      JQueryHelper.closeUsernameModal()
      indicateUsername(components, newUsername)
    }
  }

  protected def openModalUsernamePicker(components: ComponentsContainer): Unit = {
    JQueryHelper.openUsernameModal()
    components.usernameInput.focus()
  }

  protected def handleUsernameNotFound(components: ComponentsContainer): Unit = {
    if (components.usernameSpan.innerHTML.isEmpty) openModalUsernamePicker(components)
  }
}
