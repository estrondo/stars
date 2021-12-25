package webapi

import akka.Done

import java.util.concurrent.ThreadLocalRandom
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

package object impl {

  implicit class RichFuture[T](x: Future[T]) {

    def throwWith(fn: Exception => Exception)(implicit executor: ExecutionContext): Future[T] = {
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

  def generateRandomID(length: Int = 16): String = {
    val bytes = Array.ofDim[Byte](length)
    ThreadLocalRandom.current().nextBytes(bytes)
    BigInt(bytes).abs.toString(16) + System.currentTimeMillis().toHexString
  }
}
