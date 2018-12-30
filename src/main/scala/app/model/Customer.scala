package app.model

import java.util.UUID
import io.circe.generic.JsonCodec

@JsonCodec
case class Customer(id: UUID, name: String)

@JsonCodec
case class CustomerOps(name: String)
