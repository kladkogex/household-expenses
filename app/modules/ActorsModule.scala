package modules

import actors.{MonthlySummaries, TransactionImporter}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class ActorsModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindActor[MonthlySummaries]("monthly-summaries")
    bindActor[TransactionImporter]("transaction-importer")
  }
}
