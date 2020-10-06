import java.time.LocalDateTime
import slick.jdbc.H2Profile.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// Models for Users table
case class User(id: Option[Int], name: String, surname: String, date_of_birth: LocalDateTime, address: String)
case class UserWithoutId(name: String, surname: String, date_of_birth: LocalDateTime, address: String)

// Definition of the Users table
class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def surname = column[String]("surname")
  def date_of_birth = column[LocalDateTime]("date_of_birth")
  def address = column[String]("address")
  def * = (id.?, name, surname, date_of_birth, address) <> (User.tupled, User.unapply)
}

object UserData {

  val db = Database.forConfig("db")
  val users = TableQuery[Users]

  // Query for all users in the db
  def allUsers: Future[Seq[User]] = {
    db.run(users.filter(u => u.id > 0).result)
  }

  // Query for a single user
  def singleUser(id: Int): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  // Add user by id
  def insertUser(user: User): Future[User] = {
    db.run(users += user).map(_ => user)
  }

  // Update user by id
  def updateUser(id: Int, user: User): Future[User] = {
    db.run(users.filter(_.id === id).update(user)).map(_ => user)
  }

  // Delete user by id
  def deleteUser(id: Int): Future[Int] = {
    db.run(users.filter(_.id === id).delete)
  }
}
