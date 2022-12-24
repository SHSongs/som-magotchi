import zio._
import zio.Console.printLine

trait SomDo {
  def createSom(name: String): Task[Unit]
  def getSom: Task[Som]
}
trait HouseDo {
  def createHouse: IO[House.FailReason, Unit]
  def getHouses: Task[List[House]]
}
case class InMemorySom(som: Ref[Som]) extends SomDo with HouseDo {

  override def createSom(name: String): Task[Unit] =
    som.update(x => x.copy(name = name)).unit
  override def getSom: Task[Som] =
    som.get
  override def getHouses: Task[List[House]] =
    som.get.map(_.houses)

  override def createHouse: IO[House.FailReason, Unit] = for {
    hp <- som.get.map(_.hp)

    requiredHP = 20
    mayBeHouse = House.build(hp, requiredHP)

    _ <- mayBeHouse match {
      case Right(house) =>
        som
          .update(s =>
            s.copy(
              hp = hp - requiredHP,
              houses = s.houses :+ house
            )
          )
          .unit
      case Left(failReason) => ZIO.fail(failReason)
    }
  } yield ()
}

object InMemorySom {
  val layer: ZLayer[Any, Nothing, InMemorySom] = ZLayer {
    for {
      ref <- Ref.make(Som(name = "default", hp = 100))
    } yield new InMemorySom(ref)
  }
}

object Main extends ZIOAppDefault {

  val prog = for {
    _ <- ZIO.unit
    _ <- ZIO.serviceWithZIO[InMemorySom](_.createSom("good"))
    a <- ZIO.serviceWithZIO[InMemorySom](_.getSom)
    _ <- Console.printLine(a.name)

    _ <- ZIO.serviceWithZIO[InMemorySom](_.createHouse)
    _ <- ZIO.serviceWithZIO[InMemorySom](_.createHouse)
    h <- ZIO.serviceWithZIO[InMemorySom](_.getSom.map(_.houses))
    _ <- Console.printLine(h)
    hp <- ZIO.serviceWithZIO[InMemorySom](_.getSom.map(_.hp))
    _ <- Console.printLine(hp)
  } yield ()
  override def run = prog.provideSome(InMemorySom.layer)
}
