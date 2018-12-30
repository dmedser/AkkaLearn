package app

import app.service.CustomerService
import app.route.CustomerRoute
import app.config.AppConfig
import app.repository.CustomerRepository
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import doobie._
import doobie.hikari.HikariTransactor
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import pureconfig.{CamelCase, ConfigFieldMapping}
import pureconfig.generic.ProductHint
import pureconfig.generic.auto._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object Main extends IOApp {

  implicit def hint[T]: ProductHint[T] =
    ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  private val appConfig = pureconfig.loadConfig[AppConfig]("app") match {
    case Right(config) ⇒ config
    case Left(failures) ⇒
      throw LoadConfigFailureException(failures.toList.mkString("\n"))
  }

  private implicit val system: ActorSystem = ActorSystem("system")
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val transactor: Resource[IO, HikariTransactor[IO]] =
    for {
      ce ← ExecutionContexts.fixedThreadPool[IO](32)
      te ← ExecutionContexts.cachedThreadPool[IO]
      xa ← HikariTransactor.newHikariTransactor[IO](
        appConfig.db.driverClassName,
        appConfig.db.url,
        appConfig.db.user,
        appConfig.db.password,
        ce,
        te
      )
    } yield xa

  def run(args: List[String]): IO[ExitCode] = transactor.use { xa ⇒
    val customerRepository = CustomerRepository()
    val customerService = CustomerService(customerRepository, xa)
    val customerRoute = CustomerRoute(customerService)

    val serverBinding: Future[Http.ServerBinding] =
      Http().bindAndHandle(customerRoute.route, appConfig.http.host, appConfig.http.port)

    serverBinding.onComplete {
      case Success(_) ⇒
        println(s"Server online at http://${appConfig.http.host}:${appConfig.http.port}}/")
      case Failure(exception) ⇒
        throw ServerBindingFailureException(exception.getMessage)
    }

    IO(Await.result(system.whenTerminated, Duration.Inf)).as(ExitCode.Success)
  }

}
