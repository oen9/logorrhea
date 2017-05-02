package oen.logorrhea

import oen.logorrhea.components.{ComponentsContainer, ComponentsLogic}
import oen.logorrhea.materialize.JQueryHelper
import org.scalajs.dom.html

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("oen.logorrhea")
object Main {

  @JSExport
  def main(header: html.Element, main: html.Element, footer: html.Element): Unit = {
    val components = ComponentsContainer()

    val headerContent = HtmlContent.initHeader(components)
    header.appendChild(headerContent)

    val mainContent = HtmlContent.initMain(components)
    main.appendChild(mainContent)

    val footerContent = HtmlContent.initFooter()
    footer.appendChild(footerContent)

    JQueryHelper.initMaterialize()
    ComponentsLogic.initComponentsLogic(components)
  }
}
