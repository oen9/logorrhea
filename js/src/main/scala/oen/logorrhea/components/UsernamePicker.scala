package oen.logorrhea.components

import oen.logorrhea.materialize.JQueryHelper
import org.scalajs.dom
import org.scalajs.dom.{KeyboardEvent, MouseEvent}

class UsernamePicker(components: ComponentsContainer) {

  final val USERNAME_KEY = "username"

  def initPicker(connectWebSock: () => Unit, closeWebSock: () => Unit): Unit = {
    components.connectButton.onclick = (_: MouseEvent) => handleNewUsername(connectWebSock)
    components.usernameInput.onkeydown = (e: KeyboardEvent) => if ("Enter" == e.key) handleNewUsername(connectWebSock)

    components.signOutButton.onclick = (_: MouseEvent) => {
      signOut(closeWebSock)
    }

    restoreUsername(connectWebSock)
    handleUsernameNotFound()
  }

  def signOut(closeWebSock: () => Unit): Unit = {
    dom.window.localStorage.removeItem(USERNAME_KEY)
    components.usernameSpan.innerHTML = ""
    closeWebSock()
    openModalUsernamePicker()
  }

  protected def restoreUsername(connectWebSock: () => Unit): Unit = {
    Option(dom.window.localStorage.getItem(USERNAME_KEY)).foreach(username => {
      indicateUsername(username, connectWebSock)
    })
  }

  protected def indicateUsername(username: String, connectWebSock: () => Unit): Unit = {
    components.usernameSpan.innerHTML = username
    components.mutable.username = Some(username)

    connectWebSock()
  }

  protected def handleNewUsername(connectWebSock: () => Unit): Unit = {
    val newUsername = components.usernameInput.value
    if (!newUsername.isEmpty) {
      dom.window.localStorage.setItem(USERNAME_KEY, newUsername)
      indicateUsername(newUsername, connectWebSock)
    }
  }

  protected def openModalUsernamePicker(): Unit = {
    JQueryHelper.openUsernameModal()
    components.usernameInput.value = components.mutable.username.getOrElse("")
    components.usernameInput.focus()
  }

  protected def handleUsernameNotFound(): Unit = {
    if (components.mutable.username.isEmpty) openModalUsernamePicker()
  }
}
