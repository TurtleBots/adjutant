package io.github.oybek.adjutant.model

case class Build(id: Int = -1,
                 matchUp: MatchUp,
                 duration: Int,
                 ttype: BuildType,
                 patch: String,
                 author: Long = -1,
                 thumbsUp: Int = 0,
                 thumbsDown: Int = 0,
                 dictationTgId: Option[String] = None)
