package app

case class LoadConfigFailureException(description: String) extends Exception(description)
case class ServerBindingFailureException(description: String) extends Exception(description)
