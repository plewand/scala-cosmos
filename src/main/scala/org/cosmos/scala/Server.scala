package org.cosmos.scala

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{complete, _}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import org.cosmos.scala.BodyName._
import org.cosmos.scala.LittleMonadInstances.given_LittleMonad_Future
import ServiceError._

import scala.concurrent
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.language.implicitConversions
import scala.util.{Failure, Success}

object Server {

  def run(port: Int): Unit = {
    // It is possible to use Scala 2 implicits for compatibility.
    implicit val system: ActorSystem = ActorSystem("actor-system")
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    // The encoder can be integrated with AkkaHttp just with a couple of lines of code.
    // The JsonEncoder of the response type given must be provided.
    // Note that Scala 3 given is defined but properly used in place of Scala 2 implicit.
    given reponseMarshaler[A](using encoder: JsonEncoder[A]) as ToEntityMarshaller[A] = {
      Marshaller.withFixedContentType(`application/json`) { a =>
        HttpEntity(`application/json`, encoder.asJson(a))
      }
    }

    // No new keyword needed in Scala 3. LittleMonad[Future] instance have to be visible.
    val starSystemService: StarSystemService[Future] = StarSystemService[Future]
    val route = {
      ignoreTrailingSlash {
        path("api" / Segment / Segment / Segment.?){ case (starSystem, planet, moon) =>
            concat(
              parameter("environment") { _ =>
                onComplete(starSystemService.getEnvironment(starSystem.asBodyName, planet.asBodyName, moon.map(_.asBodyName))) {
                  case Success(serviceResponse) => serviceResponse match {
                    case environmentResponse: EnvironmentResponse => complete(environmentResponse)
                    case NotFoundError(msg) => complete(StatusCodes.NotFound, msg)
                    case ValidationError(msg) => complete(StatusCodes.BadRequest, msg)
                  }
                  case Failure(ex) => complete(StatusCodes.InternalServerError)
                }
              },
              pathEnd {
                onComplete(starSystemService.getFullData(starSystem.asBodyName, planet.asBodyName, moon.map(_.asBodyName))) {
                  case Success(serviceResponse) => serviceResponse match {
                    case moonResponse: MoonResponse => complete(moonResponse)
                    case planetResponse: PlanetResponse => complete(planetResponse)
                    case NotFoundError(msg) => complete(StatusCodes.NotFound, msg)
                    case ValidationError(msg) => complete(StatusCodes.BadRequest, msg)
                  }
                  case Failure(ex) => complete(StatusCodes.InternalServerError)
                }
              }
            )
        }
      }
    }


    val bindingFuture = Http().newServerAt("localhost", port).bind(route)

    usage(port)

    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  private def usage(port: Int) = {
    println(s"Server running on port $port, press any key to exit")
    println("Example requests: ")
    println(s"http://localhost:$port/api/sun/earth/moon")
    println(s"http://localhost:$port/api/sun/jupiter")
    println(s"http://localhost:$port/api/sun/earth/moon?environment")
    println(s"http://localhost:$port/api/alphacentauri/proximab?environment")
  }
}
