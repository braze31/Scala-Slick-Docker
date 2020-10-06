import akka.actor.ActorSystem
import org.scalatest.funsuite.AnyFunSuite
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class SetSuite extends AnyFunSuite {
  implicit val system = ActorSystem()
  val http = Http(system)

  test("The server should answer with OK status code") {
    http.singleRequest(HttpRequest(uri = "http://localhost:8080/users")).onComplete {
      case Success(r) => assert(r.status == StatusCodes.OK)
      case Failure(t) => assert(false)
    }
  }

  test("First user should be successfully retrieved") {
    http.singleRequest(HttpRequest(uri = "http://localhost:8080/user/1")).onComplete {
      case Success(r) => {
        assert(r.status == StatusCodes.OK)
        val answer = Await.result(r.entity.toStrict(5.seconds).map(_.data.decodeString("UTF-8")), 2.seconds)
        if (answer.contains("No such user!")) {
          assert(false)
        }
      }
      case Failure(t) => assert(false)
    }
  }

  test("User should be added") {
    http.singleRequest(HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:8080/user",
      entity = HttpEntity(ContentTypes.`application/json`, "{\"name\": \"new name\", \"surname\": \"new surname\", \"date_of_birth\": \"1989-09-01T12:00:00\", \"address\": \"St.Petersburg\"}")
    )).onComplete {
      case Success(r) => assert(r.status == StatusCodes.OK)
      case Failure(t) => assert(false)
    }
  }

  test("User should be deleted") {
    http.singleRequest(HttpRequest(
      method = HttpMethods.DELETE,
      uri = "http://localhost:8080/user/1"
    )).onComplete {
      case Success(r) => {
        assert(r.status == StatusCodes.OK)

        http.singleRequest(HttpRequest(uri = "http://localhost:8080/user/1")).onComplete {
          case Success(r) => assert(r.status == StatusCodes.NotFound)
          case Failure(t) => assert(false)
        }

      }
      case Failure(t) => assert(false)
    }
  }

}