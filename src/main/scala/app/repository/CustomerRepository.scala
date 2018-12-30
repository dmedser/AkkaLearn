package app.repository

import java.util.UUID
import app.model.{Customer, CustomerOps}
import doobie.ConnectionIO
import doobie.implicits._
import doobie.postgres.implicits._

class CustomerRepository {

  def list: ConnectionIO[List[Customer]] =
    sql"select * from customers"
      .query[Customer]
      .to[List]

  def create(customer: CustomerOps, generatedId: UUID): ConnectionIO[Int] =
    sql"insert into customers(id, name) values ($generatedId, ${customer.name})"
      .update
      .run

  def update(id: UUID, customer: CustomerOps): ConnectionIO[Int] =
    sql"update customers set name = ${customer.name} where id = $id"
      .update
      .run

  def delete(id: UUID): ConnectionIO[Int] =
    sql"delete from customers where id = $id"
      .update
      .run

}

object CustomerRepository {
  def apply(): CustomerRepository = new CustomerRepository()
}
