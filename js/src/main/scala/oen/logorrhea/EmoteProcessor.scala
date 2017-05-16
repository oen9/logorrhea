package oen.logorrhea

import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.html.Image
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

  protected def emoteTextToSpan(text: String): TypedTag[HTMLElement] = {
    EMOTE_DICT
      .get(text)
      .map(emoteIdToHtmlImg)
      .getOrElse(span(text))
  }

  protected def emoteIdToHtmlImg(id: String): TypedTag[Image] = {
    val imgSrc = s"${dom.window.location.origin}/front-res/emotes/e${id}_25.gif"
    img(`class` := "emote", src := imgSrc)
  }
}
