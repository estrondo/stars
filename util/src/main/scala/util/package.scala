import scala.concurrent.Future
import scala.language.implicitConversions

package object util {

  implicit def richFuture[T](underlying: Future[T]): RichFuture[T] =
    new RichFuture[T](underlying)

}
