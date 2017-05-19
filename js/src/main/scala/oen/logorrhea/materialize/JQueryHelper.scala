package oen.logorrhea.materialize

import org.scalajs.jquery

object JQueryHelper {
  def initMaterialize(): Unit = {
    jquery.jQuery(".dropdown-button").asInstanceOf[DropdownOperations].dropdown()
    jquery.jQuery(".emote-dropdown").asInstanceOf[DropdownOperations].dropdown(new DropdownOptions)
    jquery.jQuery(".modal").asInstanceOf[ModalOperations].modal(new ModalOptions { dismissible = false })
    refreshTooltips()
  }

  def openUsernameModal(): Unit = {
    jquery.jQuery("#user-name-modal").asInstanceOf[ModalOperations].modal("open")
  }

  def closeUsernameModal(): Unit = {
    jquery.jQuery("#user-name-modal").asInstanceOf[ModalOperations].modal("close")
  }

  def openNewRoomModal(): Unit = {
    jquery.jQuery("#new-room-modal").asInstanceOf[ModalOperations].modal("open")
  }

  def closeNewRoomModal(): Unit = {
    jquery.jQuery("#new-room-modal").asInstanceOf[ModalOperations].modal("close")
  }

  def openDeleteRoomModal: Unit = {
    jquery.jQuery("#delete-room-modal").asInstanceOf[ModalOperations].modal("open")
  }

  def closeDeleteRoomModal: Unit = {
    jquery.jQuery("#delete-room-modal").asInstanceOf[ModalOperations].modal("close")
  }

  def refreshTooltips(): Unit = {
    jquery.jQuery(".tooltipped").asInstanceOf[TooltipsOperations].tooltip(new TooltipsOptions)
  }
}
