case class House(isReady: Boolean, isClean: Boolean)

object House {
  trait FailReason
  object FailReason {
    case object LowOnHP extends FailReason
    case object Unknown extends FailReason
  }

  def build(currentHP: Int, requiredHP: Int): Either[FailReason, House] = {
    currentHP match {
      case hp if hp > requiredHP =>
        Right(House(isReady = false, isClean = false))
      case _ => Left(FailReason.LowOnHP)
    }
  }
}
