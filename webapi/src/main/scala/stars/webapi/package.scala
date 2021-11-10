package stars

package object webapi {

  implicit val commandCommandSerializer: CommandMessageSerializer = new CommandMessageSerializer
}
