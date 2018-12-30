package app.service

import java.util.UUID
import app.model.{Customer, CustomerOps}
import app.repository.CustomerRepository
import cats.effect.IO
import doobie.hikari.HikariTransactor
import doobie.implicits._

class CustomerService private (repository: CustomerRepository, transactor: HikariTransactor[IO]) {

  def list: IO[List[Customer]] =
    repository
      .list
      .transact(transactor)

  def create(customer: CustomerOps): IO[UUID] = {
    val uuid = UUID.randomUUID()
    repository
      .create(customer, uuid)
      .transact(transactor)
      .map(_ => uuid)
  }

  def update(id: UUID, customer: CustomerOps): IO[Boolean] =
    repository
      .update(id, customer)
      .transact(transactor)
      .map(_ > 0)

  def delete(id: UUID): IO[Boolean] =
    repository
      .delete(id)
      .transact(transactor)
      .map(_ > 0)

}

object CustomerService {
  def apply(repository: CustomerRepository, transactor: HikariTransactor[IO]): CustomerService =
    new CustomerService(repository, transactor)
}
