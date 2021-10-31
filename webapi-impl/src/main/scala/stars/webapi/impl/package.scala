package stars.webapi

import akka.Done

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import scala.languageFeature.implicitConversions

package object impl {

  implicit class RichFuture[T](x: Future[T]) {

    def throwWith(fn: Exception => Exception)(implicit executor: ExecutionContextExecutor): Future[T] = {
      x.transform {
        case success@Success(_) => success
        case Failure(cause: Exception) => Failure(fn(cause))
        case failure@Failure(_) => failure
      }
    }

    def toDone(implicit executor: ExecutionContextExecutor): Future[Done] = {
      for (_ <- x) yield Done
    }
  }

  val FutureDone: Future[Done] = Future.successful(Done)
}
