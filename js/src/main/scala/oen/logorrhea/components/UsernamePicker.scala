package oen.logorrhea.components

import oen.logorrhea.materialize.JQueryHelper
import org.scalajs.dom
import org.scalajs.dom.{KeyboardEvent, MouseEvent}

object UsernamePicker {

  final val USERNAME_KEY = "username"

  def initPicker(components: ComponentsContainer): Unit = {
    components.connectButton.onclick = (_: MouseEvent) => handleNewUsername(components)
    components.usernameInput.onkeydown = (e: KeyboardEvent) => if ("Enter" == e.key) handleNewUsername(components)

    components.signOutButton.onclick = (_: MouseEvent) => {
      signOut(components)
    }

    restoreUsername(components)
    handleUsernameNotFound(components)
  }

  def signOut(components: ComponentsContainer): Unit = {
    dom.window.localStorage.removeItem(USERNAME_KEY)
    components.usernameSpan.innerHTML = ""
    WebsockConnector.close(components)
    openModalUsernamePicker(components)
  }

  protected def restoreUsername(components: ComponentsContainer): Unit = {
    Option(dom.window.localStorage.getItem(USERNAME_KEY)).foreach(username => {
      indicateUsername(components, username)
    })
  }

  protected def indicateUsername(components: ComponentsContainer, username: String): Unit = {
    components.usernameSpan.innerHTML = username
    components.mutable.username = Some(username)

    WebsockConnector.connect(components)
  }

  protected def handleNewUsername(components: ComponentsContainer): Unit = {
    val newUsername = components.usernameInput.value
    if (!newUsername.isEmpty) {
      dom.window.localStorage.setItem(USERNAME_KEY, newUsername)
      indicateUsername(components, newUsername)
    }
  }

  protected def openModalUsernamePicker(components: ComponentsContainer): Unit = {
    JQueryHelper.openUsernameModal()
    components.usernameInput.value = components.mutable.username.getOrElse("")
    components.usernameInput.focus()
  }

  protected def handleUsernameNotFound(components: ComponentsContainer): Unit = {
    if (components.mutable.username.isEmpty) openModalUsernamePicker(components)
  }
}
