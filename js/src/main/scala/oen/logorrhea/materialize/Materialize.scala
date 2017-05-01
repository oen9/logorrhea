package oen.logorrhea.materialize

import scala.scalajs.js
import scala.scalajs.js.|

@js.native
trait Materialize extends js.Object {
  def toast(message: String | js.Dynamic, displayLength: Double, className: String = js.native, completeCallback: js.Function = js.native): Unit = js.native
}

@js.native
@js.annotation.JSGlobalScope
object Materialize extends js.Object {
  val Materialize: Materialize = js.native
}
