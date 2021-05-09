package io.github.oybek.adjutant.model

import java.sql.Timestamp

case class Journal(userId: Long,
                   buildId: Int,
                   timestamp: Timestamp)
