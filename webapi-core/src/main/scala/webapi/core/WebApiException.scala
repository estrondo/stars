package webapi.core

object WebApiException {

  class AlreadyExist(message: String, cause: Throwable = null) extends WebApiException(message, cause)

  class MultiMessageException(
      message: String,
      val messages: Seq[String],
      cause: Throwable = null
  ) extends WebApiException(message, cause)

  class Rejected(message: String, cause: Throwable = null) extends WebApiException(message, cause)

  class Unexpected(message: String, cause: Throwable = null) extends WebApiException(message, cause)
}

abstract class WebApiException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)
