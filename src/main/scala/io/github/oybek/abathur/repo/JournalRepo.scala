package io.github.oybek.abathur.repo

import io.github.oybek.abathur.model.Journal

trait JournalRepo[DB[_]] {
  def add(journal: Journal): DB[Unit]
}
