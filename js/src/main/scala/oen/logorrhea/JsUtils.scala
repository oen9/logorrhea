package oen.logorrhea

import scala.scalajs.js.JSStringOps

object JsUtils {
  def localeCompare(s1: String, s2: String): Int = {
    JSStringOps.enableJSStringOps(s1).localeCompare(s2)
  }
}
