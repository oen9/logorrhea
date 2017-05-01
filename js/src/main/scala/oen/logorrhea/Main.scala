package oen.logorrhea

import oen.logorrhea.components.{ComponentsContainer, ComponentsLogic}
import oen.logorrhea.materialize.{DropdownOperations, Materialize, ModalOperations, ModalOptions}
import org.scalajs.dom.{html, _}
import org.scalajs.jquery

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("oen.logorrhea")
object Main {

  @JSExport
  def main(header: html.Element, main: html.Element, footer: html.Element): Unit = {
    val components = ComponentsContainer()
    ComponentsLogic.initComponentsLogic(components)

    val headerContent = HtmlContent.initHeader(components)
    header.appendChild(headerContent)

    val mainContent = HtmlContent.initMain(components)
    main.appendChild(mainContent)

    val footerContent = HtmlContent.initFooter()
    footer.appendChild(footerContent)

    initMaterializeJs()
  }

  def initMaterializeJs(): Unit = {
    jquery.jQuery(".dropdown-button").asInstanceOf[DropdownOperations].dropdown()
    jquery.jQuery(".modal").asInstanceOf[ModalOperations].modal(new ModalOptions { dismissible = false })

    jquery.jQuery("#user-name-modal").asInstanceOf[ModalOperations].modal("open")
  }
}
