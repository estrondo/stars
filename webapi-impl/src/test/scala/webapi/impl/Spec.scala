package webapi.impl

import org.scalatest.freespec.{AnyFreeSpecLike, AsyncFreeSpecLike}
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}

trait BaseSpec extends Matchers with BeforeAndAfterEach with BeforeAndAfterAll {
  this: Suite =>
}

trait Spec extends AnyFreeSpecLike with BaseSpec

trait AsyncSpec extends AsyncFreeSpecLike with BaseSpec
