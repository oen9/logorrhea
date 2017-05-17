package oen.logorrhea.materialize

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

@ScalaJSDefined
class DropdownOptions extends js.Object {
  var constrainWidth: Boolean = false
}

@js.native
trait DropdownOperations extends js.Object {
  def dropdown(): Unit = js.native
  def dropdown(options: DropdownOptions): Unit = js.native
}
