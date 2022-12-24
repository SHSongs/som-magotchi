import io.getquill.{CamelCase, SnakeCase}
import io.getquill.jdbczio.Quill
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

object Main extends ZIOAppDefault {

  val prog = for {
    _ <- ZIO.serviceWithZIO[SomRepositoryPostgres](_.createHouse)
    _ <- ZIO.serviceWithZIO[SomRepositoryPostgres](_.createHouse)
    a <- ZIO.serviceWithZIO[SomRepositoryPostgres](_.getHouses)
    _ <- printLine(a)
  } yield ()
  override def run =
    prog.provideSome(
      SomRepositoryPostgres.layer,
      Quill.Postgres.fromNamingStrategy(CamelCase),
      Quill.DataSource.fromPrefix("somDatabaseConfig")
    )
}
