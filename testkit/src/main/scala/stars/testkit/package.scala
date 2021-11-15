package stars

import scala.util.Random

package object textkit {

  def addSystemProperty(properties: (String, String)*): Unit = {
    for ((k, v) <- properties if System.getProperty(k) == null)
      System.setProperty(k, v)
  }

  def newRandomId(): String = {
    val bytes = Array.ofDim[Byte](32)
    Random.nextBytes(bytes)
    BigInt(bytes).abs.toString(32)
  }
}
