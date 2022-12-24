import zio._
import zio.test._

object AppSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("house")(
    test("test") {
      for {
        _ <- ZIO.unit
        _ <- ZIO.serviceWithZIO[InMemorySom](_.createSom("good"))
        a <- ZIO.serviceWithZIO[InMemorySom](_.getSom)

        _ <- ZIO.serviceWithZIO[InMemorySom](_.createHouse)
        h <- ZIO.serviceWithZIO[InMemorySom](_.getSom.map(_.houses))
        hp <- ZIO.serviceWithZIO[InMemorySom](_.getSom.map(_.hp))
        _ <- Console.print(hp)

        questionVector <- TestConsole.output
        q1 = questionVector(0)

      } yield assertTrue(q1 == "80")
    },
    test("fail build") {
      for {
        _ <- ZIO.unit
        _ <- ZIO.serviceWithZIO[InMemorySom](_.createSom("good"))
        a <- ZIO.serviceWithZIO[InMemorySom](_.getSom)
        _ <- Console.printLine(a.name)

        hp <- ZIO.serviceWithZIO[InMemorySom](_.getSom.map(_.hp))
        _ <- Console.printLine(hp)

        _ <- ZIO.serviceWithZIO[InMemorySom](_.createHouse)
        _ <- ZIO.serviceWithZIO[InMemorySom](_.createHouse)
        _ <- ZIO.serviceWithZIO[InMemorySom](_.createHouse)
        _ <- ZIO.serviceWithZIO[InMemorySom](_.createHouse)
        _ <- ZIO.serviceWithZIO[InMemorySom](_.createHouse)
        _ <- ZIO.serviceWithZIO[InMemorySom](_.createHouse)
        _ <- ZIO.serviceWithZIO[InMemorySom](_.createHouse)
        _ <- ZIO.serviceWithZIO[InMemorySom](_.createHouse)
        _ <- ZIO.serviceWithZIO[InMemorySom](_.createHouse)
        _ <- ZIO.serviceWithZIO[InMemorySom](_.createHouse)
      } yield assertCompletes
    } @@ TestAspect.failing
  ).provide(InMemorySom.layer)
}