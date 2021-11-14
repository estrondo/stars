package stars.webapi.impl

import org.scalatest.freespec.{AnyFreeSpecLike, AsyncFreeSpecLike}
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}

trait BaseSpec extends Matchers with BeforeAndAfterEach with BeforeAndAfterAll {
  this: Suite =>

  def addSystemProperty(properties: (String, String)*): Unit = {
    for ((k, v) <- properties if System.getProperty(k) == null)
      System.setProperty(k, v)
  }
}

trait Spec extends AnyFreeSpecLike with BaseSpec

trait AsyncSpec extends AsyncFreeSpecLike with BaseSpec
