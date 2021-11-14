package stars.webapi.impl

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.freespec.{AnyFreeSpecLike, AsyncFreeSpecLike}
import org.scalatest.matchers.should.Matchers

trait SpecHelper {

  def addSystemProperty(properties: (String, String)*): Unit = {
    for ((k, v) <- properties if System.getProperty(k) == null)
      System.setProperty(k, v)
  }
}

trait Spec extends AnyFreeSpecLike with Matchers with BeforeAndAfterEach with BeforeAndAfterAll with SpecHelper

trait AsyncSpec extends AsyncFreeSpecLike with Matchers with BeforeAndAfterEach with BeforeAndAfterAll with SpecHelper
