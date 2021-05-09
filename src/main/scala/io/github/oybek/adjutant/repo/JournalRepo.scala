package io.github.oybek.adjutant.repo

import io.github.oybek.adjutant.model.Journal

trait JournalRepo[DB[_]] {
  def add(journal: Journal): DB[Unit]
}
