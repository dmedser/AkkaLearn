package app.route

import java.util.UUID
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import app.model.Customer._
import app.model.{Customer, CustomerOps}
import app.service.CustomerService
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.JsonCodec
import scala.concurrent.ExecutionContext

@JsonCodec
case class Response[T](body: T, statusCode: Int, reason: String)

class CustomerRoute private (customerService: CustomerService)(implicit ec: ExecutionContext) {

  private val exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case _ =>
        complete(Response(None, 500, "Something went wr0ng!"))
    }

  val route: Route = cors() {
    handleExceptions(exceptionHandler) {
      pathPrefix("customers") {
        list ~
          create ~
          update ~
          delete
      }
    }
  }

  private def list: Route =
    get {
      onSuccess(customerService.list.unsafeToFuture()) { customersList ⇒
        complete(Response(customersList, 200, ""))
      }
    }

  private def create: Route = pathPrefix("create") {
    post {
      entity(as[CustomerOps]) { customerToCreate =>
        onSuccess(customerService.create(customerToCreate).unsafeToFuture()) { createdCustomerUUID =>
          complete(Response(Customer(createdCustomerUUID, customerToCreate.name), 200, ""))
        }
      }
    }
  }

  private def update: Route = pathPrefix("update") {
    parameter('id) { id =>
      post {
        entity(as[CustomerOps]) { updatedCustomer =>
          val uuid = UUID.fromString(id)
          onSuccess(customerService.update(uuid, updatedCustomer).unsafeToFuture) { customerIsUpdated =>
            if (customerIsUpdated) {
              complete(Response(updatedCustomer.name, 200, ""))
            } else {
              complete(Response(None, 400, "Customer not found"))
            }
          }
        }
      }
    }
  }

  private def delete: Route = pathPrefix("delete") {
    parameter('id) { id =>
      post {
        val uuid = UUID.fromString(id)
        onSuccess(customerService.delete(uuid).unsafeToFuture()) { customerIsDeleted =>
          if (customerIsDeleted) {
            complete(Response(None, 200, ""))
          } else {
            complete(Response(None, 400, "Customer not found"))
          }
        }
      }
    }
  }

}

object CustomerRoute {
  def apply(customerService: CustomerService)(implicit ec: ExecutionContext): CustomerRoute =
    new CustomerRoute(customerService)
}
