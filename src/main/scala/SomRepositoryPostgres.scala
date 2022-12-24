import io.getquill._
import io.getquill.jdbczio.Quill
import zio.{IO, Task, ZLayer}
class SomRepositoryPostgres(quill: Quill[PostgresDialect, CamelCase])
    extends SomDo
    with HouseDo {
  import quill._
  override def createSom(name: String): Task[Unit] = ???

  override def getSom: Task[Som] = ???
  override def createHouse: IO[House.FailReason, Unit] = run(
    query[House].insertValue(lift(House(isReady = false, isClean = false)))
  ).orElseFail(House.FailReason.Unknown).unit
  override def getHouses: Task[List[House]] = run(query[House])
}

object SomRepositoryPostgres {
  val layer = ZLayer.fromFunction(new SomRepositoryPostgres(_))

}
