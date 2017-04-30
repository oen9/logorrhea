package oen

import org.scalajs.{dom, jquery}
import org.scalajs.dom.html.{Div, Input}
import org.scalajs.dom.{KeyboardEvent, MouseEvent, html}

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel, ScalaJSDefined}
import scalatags.JsDom.all._
import scala.scalajs.js

@js.native
@js.annotation.JSGlobalScope
object Materializecss extends js.Object {
  var Materialize: Materialize = js.native
}

@ScalaJSDefined
class ModalOptions extends js.Object{
  var dismissible: Boolean = true
}

@js.native
trait ModalTest extends js.Object{
  def modal(action: String): Unit = js.native
  def modal(options: ModalOptions): Unit = js.native
}

@JSExportTopLevel("oen")
object Main {

  def sendMsg(inputText: Input, msgDivRnr: Div): Unit = {
    val newText = inputText.value

    if (!newText.isEmpty) {
      Materializecss.Materialize.toast(inputText.value, 4000)

      msgDivRnr.appendChild(div(inputText.value).render)
      msgDivRnr.scrollTop = msgDivRnr.scrollHeight

      inputText.value = ""
    }

    inputText.focus()
  }

  @JSExport
  def main(target: html.Div): Unit = {

    val fooDivs = (1 to 20).map(x => div(p("foo")))
    val fooLi = (1 to 40).map(x => li(a(`class` := "waves-effect btn-flat", href := "#", "baz")))


    val leftMenu = div(`class` := "col s12 m4 l1 center",
      div(`class` := "blue lighten-5 scrollable-content",
        ul(fooLi)
      ),
      a(`class` := "btn-floating btn waves-effect waver-light", i(`class` := "material-icons", "add"))
    )

    val msgDiv = div(`class` := "blue lighten-5 scrollable-content left-align",
      fooDivs
    )
    val msgDivRnr = msgDiv.render

    val inputText = input(`type` := "text", placeholder := "Type here....").render
    inputText.onkeydown = (e: KeyboardEvent) => {
      if ("Enter" == e.key) {
        sendMsg(inputText, msgDivRnr)
      }
    }

    val button = p(`class` := "btn waves-effect waves-light", i(`class` := "material-icons", "send")).render
    button.onclick = (e: MouseEvent) => sendMsg(inputText, msgDivRnr)


    val senderInput = div(`class` := "col s12 m10 l10", inputText)
    val senderButton = div(`class` := "col s12 m2 l2", button)
    val senderRow = div(`class` := "row", senderInput, senderButton)

    val middleContent = div(`class` := "col s12 m8 l10 center", msgDivRnr, senderRow)

    val rightList = div(`class` := "col s12 m4 l1 blue lighten-5 scrollable-content",
      ul(fooLi)
    )

    val mainContentRow = div(`class` := s"row", leftMenu, middleContent, rightList)
    target.appendChild(mainContentRow.render)

    msgDivRnr.scrollTop = msgDivRnr.scrollHeight

    jquery.jQuery("#modal1").asInstanceOf[ModalTest].modal("open")
  }
}
