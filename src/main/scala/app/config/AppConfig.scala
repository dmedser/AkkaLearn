package app.config

import app.config.AppConfig._

case class AppConfig(db: DbConfig, http: HttpConfig)

object AppConfig {

  case class DbConfig(
    driverClassName: String,
    url: String,
    user: String,
    password: String
  )
  
  case class HttpConfig(host: String, port: Int)

}
