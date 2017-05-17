package oen.logorrhea

import oen.logorrhea.components.ComponentsContainer
import org.scalajs.dom
import org.scalajs.dom.{MouseEvent, html}
import org.scalajs.dom.html.{Image, UList}
import org.scalajs.dom.raw.HTMLElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

object EmoteProcessor {

  val EMOTE_REGEX = """:([\w]+):"""

  val EMOTE_DICT = Map(
    ":meh:" -> "1",
    ":zombie:" -> "2",
    ":blink:" -> "3",
    ":ym:" -> "4",
    ":wut:" -> "5",
    ":eyes:" -> "6",
    ":sad:" -> "7",
    ":heh:" -> "8",
    ":xxx:" -> "9",
    ":devil:" -> "10"
  )

  def process(text: String): html.Span = {
    val withoutEmotes = text
      .split(EMOTE_REGEX)
      .map(t => span(t))

    val onlyEmotes = EMOTE_REGEX.r
      .findAllIn(text)
      .toList
      .map(emoteTextToSpan)

    val fullMsg = withoutEmotes
      .zipAll(onlyEmotes, span(), span())
      .foldLeft(Vector[TypedTag[HTMLElement]]())((v, e) => v ++ Vector(e._1, e._2))

    span(fullMsg).render
  }

  def genEmoteList(components: ComponentsContainer): TypedTag[UList] = {
    ul(`class` := "dropdown-content", id := "emote-dropdown",
      EMOTE_DICT
        .grouped(3)
        .map(g => li(g.map(e => genEmoteListElement(e, components)).toList))
        .toList
    )
  }

  protected def genEmoteListElement(emote: (String, String), components: ComponentsContainer): Image = {
    val element = emoteIdToHtmlImg(emote._1, emote._2, 100).render
    element.onclick = (_: MouseEvent) => {
      components.messageInput.value += s" ${emote._1} "
      components.messageInput.focus()
    }
    element
  }

  protected def emoteTextToSpan(text: String): TypedTag[HTMLElement] = {
    EMOTE_DICT
      .get(text)
      .map(id => emoteIdToHtmlImg(text, id, 25))
      .getOrElse(span(text))
  }

  protected def emoteIdToHtmlImg(emoteText: String, emoteId: String, size: Int): TypedTag[Image] = {
    val imgSrc = s"${dom.window.location.origin}/front-res/emotes/e${emoteId}_$size.gif"
    img(`class` := "emote tooltipped", src := imgSrc, attr("data-tooltip") := emoteText)
  }
}
