package webapi.impl.protocol

import com.fasterxml.jackson.annotation.JsonTypeInfo

object Attempt {

  case object Empty extends Attempt[Nothing, Nothing]

  case class Cause(kind: String, message: String, stack: Seq[StackElement], cause: Option[Cause]) {

    def toThrowable: ThrowableCause = new ThrowableCause(this)

  }

  case class Failure[+S, +F](value: F, cause: Cause) extends Attempt[S, F]

  case class StackElement(className: String, methodName: String, fileName: String, lineNumber: Int)

  case class Success[+S, +F](value: S) extends Attempt[S, F]

  class ThrowableCause(cause: Cause, nested: Option[ThrowableCause]) extends RuntimeException(cause.message, nested.orNull) {

    def this(source: Cause) = this(source, source.cause match {
      case Some(nested) => Some(new ThrowableCause(nested))
      case _ => None
    })

    override def fillInStackTrace(): Throwable = this


    override def getMessage: String = cause.kind + " - " + cause.message

    override def getStackTrace: Array[StackTraceElement] = {

      val view = for (element <- cause.stack.view) yield {
        new StackTraceElement(element.className, element.methodName, element.fileName, element.lineNumber)
      }

      view.toArray
    }
  }
}

@JsonTypeInfo(
  use = JsonTypeInfo.Id.MINIMAL_CLASS,
  include = JsonTypeInfo.As.PROPERTY,
  property = "@type"
)
sealed trait Attempt[+S, +F]
