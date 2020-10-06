import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout

import spray.json._
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.DurationInt
import scala.io.StdIn

// Collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  // Serialize support for LocalDateTime
  implicit val localDateTimeFormat = new JsonFormat[LocalDateTime] {
    private val iso_date_time = DateTimeFormatter.ISO_DATE_TIME

    def write(x: LocalDateTime) = JsString(iso_date_time.format(x))

    def read(value: JsValue) = value match {
      case JsString(x) => LocalDateTime.parse(x, iso_date_time)
      case x => throw new RuntimeException(s"Unexpected type ${x.getClass.getName} when trying to parse LocalDateTime")
    }
  }
  // possible formats for Users table
  implicit val userFormat = jsonFormat5(User)
  implicit val userWithoutId = jsonFormat4(UserWithoutId)
}


object Main extends App with JsonSupport {
  val cfgFactory = ConfigFactory.load()
  val host = cfgFactory.getString("server.host")
  val port = cfgFactory.getInt("server.port")

  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(20.seconds)

  // Routes handlers
  val route: Route = {
    // Get all users from db
    concat(
      get {
        concat(path("users") {
          onSuccess(UserData.allUsers) {
            case users: Seq[User] =>
              complete(users)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        },
          // Get user from db by id
          path("user" / IntNumber) { id =>
            onSuccess(UserData.singleUser(id)) {
              case Some(user) => complete(user)
              case None => complete(StatusCodes.NotFound -> "{\"message\": \"No such user!\"}")
            }
          })
      },
      post {
        // post user in db from json(User) format
        path("user") {
          entity(as[User]) { user =>
            onSuccess(UserData.insertUser(user)) {
              case user: User =>
                complete(user)
              case _ =>
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      },
      delete {
        // Delete user from db by id
        // if id not found in db -> case 0 and error message
        // if id exist in db -> delete user by id and message that user with this id was deleted
        path("user" / IntNumber) { id =>
          onSuccess(UserData.deleteUser(id)) {
            case 0 => complete(StatusCodes.NotFound -> s"""{"message": "No such user with id $id"}""")
            case _ => complete(s"""{"message": "Deleted user with id $id"}""")
          }
        }
      },
      post {
        // Update user in db
        path(pm = "user" / "update" / IntNumber) { id =>
          // using model UserWithoutId by jsonFormat4
          entity(as[UserWithoutId]) { user =>
            onSuccess(UserData.updateUser(id, User(Some(id), user.name, user.surname, user.date_of_birth, user.address))) {
              case user: User =>
                complete(user)
              case _ =>
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    )
  }

  // bind our server to host and port
  val bindingFuture = Http().newServerAt(host, port).bind(route)
  println(s"Server is running at $host:$port\n")
  StdIn.readLine()

  system.terminate()
}
