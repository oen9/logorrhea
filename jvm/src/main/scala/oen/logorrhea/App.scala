package oen.logorrhea

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import oen.logorrhea.actors.WebsockDispatcherActor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object App {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val websockDispatcher = system.actorOf(WebsockDispatcherActor.props, "websock-dispatcher")

    val api = new AppServiceApi(system, websockDispatcher)

    val config = ConfigFactory.load()
    val host = config.getString("http.host")
    val port = config.getInt("http.port")

    val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(api.routes, host, port = port)

    val log =  Logging(system.eventStream, "app-service")

    bindingFuture.onComplete {
      case Success(serverBinding) =>
        log.info("Bound to {}", serverBinding.localAddress)
      case  Failure(t) =>
        log.error(t, "Failed to bind to {}:{}!", host, port)
        system.terminate()
    }
  }
}
