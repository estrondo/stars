package util

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class RichFuture[T](underlying: Future[T]) {

  def asTry(implicit executor: ExecutionContext): Future[Try[T]] = underlying.transform(value => Success(value))

  def throwBy(
      decorator: Throwable => Throwable
  )(implicit executor: ExecutionContext): Future[T] = {
    underlying.transform {
      case success @ Success(_)      => success
      case Failure(cause: Exception) => Failure(decorator(cause))
      case failure @ Failure(_)      => failure
    }
  }
}
