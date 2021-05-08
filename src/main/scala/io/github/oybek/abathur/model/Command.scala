package io.github.oybek.abathur.model

case class Command(supply: Int,
                   whenDo: Int,
                   whatDo: String,
                   buildId: Int = -1)
