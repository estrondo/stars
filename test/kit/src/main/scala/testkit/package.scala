import scala.util.Random

package object testkit {

  def addSystemProperty(properties: (String, String)*): Unit = {
    for ((k, v) <- properties if System.getProperty(k) == null)
      System.setProperty(k, v)
  }

  def newRandomId(): String = {
    val bytes = Array.ofDim[Byte](16)
    Random.nextBytes(bytes)
    BigInt(bytes).abs.toString(32)
  }
}
