package stars.webapi

import akka.Done

import java.util.UUID
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

package object impl {

  implicit class RichFuture[T](x: Future[T]) {

    def throwWith(fn: Exception => Exception)(implicit executor: ExecutionContextExecutor): Future[T] = {
      x.transform {
        case success@Success(_) => success
        case Failure(cause: Exception) => Failure(fn(cause))
        case failure@Failure(_) => failure
      }
    }

    def toDone(implicit executor: ExecutionContext): Future[Done] = {
      for (_ <- x) yield Done
    }

    def toTry()(implicit executor: ExecutionContext): Future[Try[T]] = {
      x.transform(Success(_))
    }
  }

  val FutureDone: Future[Done] = Future.successful(Done)
}
