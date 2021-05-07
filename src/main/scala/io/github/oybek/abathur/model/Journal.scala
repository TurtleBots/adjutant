package io.github.oybek.abathur.model

import java.sql.Timestamp

case class Journal(userId: Long,
                   buildId: Int,
                   timestamp: Timestamp)
