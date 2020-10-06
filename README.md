This project uses Akka Http for the server and Slick for database access.

`Main.scala` contains the server and the routes for the endpoints, while `UserData.scala` contains all the database
interaction stuff.

# Prerequisites:

- Scala [Akka http, Slick]
- Sbt
- Docker
- Docker-compose

# How to run:

The server will run on `localhost:8080`.
Steps to run the project:

```
$ docker-compose up -d
$ sbt run
```

### Use curl to access main features:

`GET /users` -- get all users in the database

`GET /user/id` -- get single user by id

`POST /user` -- add new user

`DELETE /user/id` -- delete user by id

`UPDATE /user/id` -- update user by id

---


# How to use it:

### Get all users

```
$ curl -w "\n" -X GET localhost:8080/users

[{"address":"St.Petersburg","date_of_birth":"1989-09-01T12:00:00","id":1,"name":"Artem","surname":"Avvakumov"},{"address":"St.Petersburg","date_of_birth":"1989-09-01T12:00:00","id":2,"name":"Random","surname":"Testovich"}]
```

### Get a particular user (by id):

```
$ curl -w "\n" -X GET localhost:8080/user/1

{"address":"St.Petersburg","date_of_birth":"1989-09-01T12:00:00","id":1,"name":"Artem","surname":"Avvakumov"}
```

### Add user (providing name, surname, date_of_birth and address):

```
$ curl -w "\n" -X POST -H "Content-Type: application/json" -d '{"name": "new name", "surname": "new surname", "date_of_birth": "1989-09-01T12:00:00", "address": "St.Petersburg"}' localhost:8080/user

{"address":"St.Petersburg","date_of_birth":"1989-09-01T12:00:00","name":"new name","surname":"new surname"}

$ curl -w "\n" -X GET localhost:8080/users

[{"address":"St.Petersburg","date_of_birth":"1989-09-01T12:00:00","id":1,"name":"Artem","surname":"Avvakumov"},{"address":"St.Petersburg","date_of_birth":"1989-09-01T12:00:00","id":2,"name":"Random","surname":"Testovich"},{"address":"St.Petersburg","date_of_birth":"1989-09-01T12:00:00","id":3,"name":"new name","surname":"new surname"}]
```

### Update user (by id):

```
$ curl -w "\n" -X POST -H "Content-Type: application/json" -d '{"name":"Name","surname": "Surname", "date_of_birth":"1989-09-01T12:00:00", "address":"St.Petersburg"}' localhost:8080/user/update/1

{"address":"St.Petersburg","date_of_birth":"1989-09-01T12:00:00","id":1,"name":"Name","surname":"Surname"}

$ curl -w "\n" -X GET localhost:8080/users

[{"address":"St.Petersburg","date_of_birth":"1989-09-01T12:00:00","id":2,"name":"Random","surname":"Testovich"},{"address":"St.Petersburg","date_of_birth":"1989-09-01T12:00:00","id":3,"name":"new name","surname":"new surname"},{"address":"St.Petersburg","date_of_birth":"1989-09-01T12:00:00","id":1,"name":"Name","surname":"Surname"}]
```

### Delete user (by id):

```
$ curl -w "\n" -X DELETE localhost:8080/user/3
Deleted user with id 3

$ curl -w "\n" -X GET localhost:8080/user/3
No such user!

$ curl -w "\n" -X GET localhost:8080/users

[{"address":"St.Petersburg","date_of_birth":"1989-09-01T12:00:00","id":2,"name":"Random","surname":"Testovich"},{"address":"St.Petersburg","date_of_birth":"1989-09-01T12:00:00","id":1,"name":"Name","surname":"Surname"}]

```
