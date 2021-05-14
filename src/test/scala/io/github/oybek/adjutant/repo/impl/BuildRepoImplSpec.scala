package io.github.oybek.adjutant.repo.impl

import io.github.oybek.adjutant.model.{Build, BuildType, Donors}
import io.github.oybek.adjutant.service.qlang.QueryLang.{And, Const, Expr}
import io.github.oybek.adjutant.service.qlang.QueryLangParser
import io.github.oybek.adjutant.service.qlang.QueryLangParser.sumExpr
import munit.FunSuite
import slick.ast.BaseTypedType
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

class BuildRepoImplSpec extends FunSuite with PostgresSpec with Donors {


  test("BuildRepoImplSpec ") {
    withContainers { postgresContainer =>
      val db = createDbRunner(postgresContainer)
      val buildRepoImpl = new BuildRepoImpl

      def parse(raw: String): Expr =
        QueryLangParser.parse(raw).toOption.get

      db.run(
        for {
          generatedId <- buildRepoImpl.add(buildDonor)
          _ = assertEquals(generatedId, 1)

          build <- buildRepoImpl.getById(generatedId)
          _ = assertEquals(build, Some(buildDonor.copy(id = generatedId)))

          builds <- buildRepoImpl.getByQuery(Const(buildDonor.matchUp))
          _ = assertEquals(builds, Seq(buildDonor.copy(id = generatedId)))

          builds <- buildRepoImpl.getByQuery(
            And(
              Const(buildDonor.matchUp),
              Const(BuildType.values.find(_ != buildDonor.ttype).get)
            )
          )
          _ = assertEquals(builds, Seq.empty[Build])

          _ <- buildRepoImpl.thumbUp(generatedId)
          _ <- buildRepoImpl.thumbDown(generatedId)
          _ <- buildRepoImpl.setDictationTgId(generatedId, "123")
          build <- buildRepoImpl.getById(generatedId)
          _ = assertEquals(
            build,
            Some(
              buildDonor.copy(
                id = generatedId,
                thumbsUp = buildDonor.thumbsUp + 1,
                thumbsDown = buildDonor.thumbsDown + 1,
                dictationTgId = Some("123")
              )
            )
          )
        } yield ()
      )
    }
  }
}
