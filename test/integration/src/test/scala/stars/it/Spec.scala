package stars.it

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, OptionValues}

trait Spec
  extends AnyFreeSpecLike
    with BeforeAndAfterAll
    with Matchers
    with OptionValues
    with ScalaFutures
