package simple

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Properties

object Server {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val route = {
      get {
        pathSingleSlash {
          getFromResource("index-dev.html")
        } ~
        pathPrefix("front-res") {
          getFromResourceDirectory("front-res")
        } ~
        pathPrefix("logorrhea-fastopt.js") {
          getFromResource("logorrhea-fastopt.js")
        }
      }
    }

    val port = Properties.envOrElse("PORT", "8080").toInt
    Http().bindAndHandle(route, "0.0.0.0", port = port)
  }
}
