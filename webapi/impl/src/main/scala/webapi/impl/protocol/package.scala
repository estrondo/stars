package webapi.impl

import webapi.impl.protocol.Attempt.{Cause, StackElement}

import scala.language.implicitConversions

package object protocol {

  implicit def convertToCause(throwable: Throwable): Cause = {
    val kind = throwable.getClass.getName
    val message = throwable.getMessage
    val view = for (element <- throwable.getStackTrace.view) yield {
      StackElement(element.getClassName, element.getMethodName, element.getFileName, element.getLineNumber)
    }

    if (throwable.getCause != null && throwable.getCause != throwable) {
      Cause(kind, message, view.toSeq, Some(convertToCause(throwable.getCause)))
    } else {
      Cause(kind, message, view.toSeq, None)
    }
  }
}
