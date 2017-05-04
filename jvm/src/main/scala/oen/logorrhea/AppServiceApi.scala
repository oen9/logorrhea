package oen.logorrhea

import akka.NotUsed
import akka.pattern._
import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.Timeout
import oen.logorrhea.actors.{UserActor, WebsockDispatcherActor}
import oen.logorrhea.models.Data

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong

class AppServiceApi(
  val system: ActorSystem,
  val websockDispatcher: ActorRef
) extends AppService

trait AppService {

  implicit val system: ActorSystem
  val websockDispatcher: ActorRef

  val routes: Route = getStatic ~
    getStaticDev ~
    websock

  def getStatic: Route = get {
    pathSingleSlash {
      getFromResource("index.html")
    } ~
    path("logorrhea-opt.js") {
      getFromResource("logorrhea-opt.js")
    } ~
    pathPrefix("front-res") {
      getFromResourceDirectory("front-res")
    }
  }

  def getStaticDev: Route = get {
    path("dev") {
      getFromResource("index-dev.html")
    } ~
    path("logorrhea-fastopt.js") {
      getFromResource("logorrhea-fastopt.js")
    } ~
    path("logorrhea-fastopt.js.map") {
      getFromResource("logorrhea-fastopt.js.map")
    }
  }

  def websock: Route = path("websock") {
    onSuccess(createUser()) (
      handleWebSocketMessages
    )
  }

  def createUser(): Future[Flow[Message, Message, NotUsed]] = {

    implicit val timeout = Timeout(5 seconds)
    implicit val ex = system.dispatcher

    val createdUser = websockDispatcher ? WebsockDispatcherActor.CreateUser

    createdUser.mapTo[ActorRef].map(userActor => {
      val inMsgFlow = Flow[Message]
        .map {
          case TextMessage.Strict(msgText) => Data.fromJson(msgText)
          case _ => NotUsed
        }.to(Sink.actorRef(userActor, PoisonPill))

      val outMsgFlow = Source.actorRef[Data](Int.MaxValue, OverflowStrategy.dropTail)
        .mapMaterializedValue(outActor => {
          userActor ! UserActor.WebsockOutput(outActor)
          NotUsed
        }).map { data: Data => TextMessage(Data.toJson(data)) }

      Flow.fromSinkAndSource(inMsgFlow, outMsgFlow)
    })
  }


}
