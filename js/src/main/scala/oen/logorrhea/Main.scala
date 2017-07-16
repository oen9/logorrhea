package oen.logorrhea

import oen.logorrhea.components.{ComponentsContainer, ComponentsLogic, UsernamePicker, WebsockConnector}
import oen.logorrhea.materialize.JQueryHelper
import org.scalajs.dom.html

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("oen.logorrhea")
object Main {

  @JSExport
  def main(header: html.Element, main: html.Element, footer: html.Element): Unit = {
    val components = ComponentsContainer()
    val htmlContent = new HtmlContent(components)

    val usernamePicker = new UsernamePicker(components)
    val websockConnector = new WebsockConnector(htmlContent, components, usernamePicker)
    val componentsLogic = new ComponentsLogic(websockConnector)

    htmlContent.init(header, main, footer)
    JQueryHelper.initMaterialize()
    componentsLogic.initComponentsLogic(components)
    usernamePicker.initPicker(websockConnector.connect, websockConnector.close)
  }
}
