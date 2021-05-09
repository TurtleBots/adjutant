package io.github.oybek.adjutant.model

case class Command(supply: Int,
                   whenDo: Int,
                   whatDo: String,
                   buildId: Int = -1)
