package webapi

import scala.concurrent.ExecutionContext

trait WithExecutionContext {

  protected implicit def executionContext: ExecutionContext
}
