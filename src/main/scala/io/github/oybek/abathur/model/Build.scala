package io.github.oybek.abathur.model

case class Build(id: Int,
                 matchUp: MatchUp,
                 duration: Int,
                 ttype: BuildType,
                 patch: String,
                 author: String,
                 thumbsUp: Int,
                 thumbsDown: Int,
                 dictationTgId: Option[String])
