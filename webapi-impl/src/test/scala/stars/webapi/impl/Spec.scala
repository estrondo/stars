package stars.webapi.impl

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.freespec.{AnyFreeSpecLike, AsyncFreeSpecLike}
import org.scalatest.matchers.should.Matchers

trait Spec extends AnyFreeSpecLike with Matchers with BeforeAndAfterEach with BeforeAndAfterAll

trait AsyncSpec extends AsyncFreeSpecLike with Matchers with BeforeAndAfterEach with BeforeAndAfterAll
