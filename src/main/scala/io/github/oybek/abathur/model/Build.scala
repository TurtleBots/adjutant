package io.github.oybek.abathur.model

case class Build(id: Int = -1,
                 matchUp: MatchUp,
                 duration: Int,
                 ttype: BuildType,
                 patch: String,
                 author: Option[String] = None,
                 thumbsUp: Int = 0,
                 thumbsDown: Int = 0,
                 dictationTgId: Option[String] = None)
