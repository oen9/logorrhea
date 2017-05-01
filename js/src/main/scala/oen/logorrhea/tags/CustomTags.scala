package oen.logorrhea.tags

import org.scalajs.dom
import org.scalajs.dom.html.Element

import scalatags.generic
import scalatags.jsdom.TagFactory

trait CustomTags extends generic.Tags[dom.Element, dom.Element, dom.Node] with TagFactory {
  lazy val nav: ConcreteHtmlTag[Element] = typedTag[dom.html.Element]("nav")
}
