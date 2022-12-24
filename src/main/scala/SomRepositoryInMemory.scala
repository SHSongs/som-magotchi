import zio.{IO, Ref, Task, ZIO, ZLayer}

case class SomRepositoryInMemory(som: Ref[Som]) extends SomDo with HouseDo {

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

object SomRepositoryInMemory {
  val layer: ZLayer[Any, Nothing, SomRepositoryInMemory] = ZLayer {
    for {
      ref <- Ref.make(Som(name = "default", hp = 100))
    } yield new SomRepositoryInMemory(ref)
  }
}
