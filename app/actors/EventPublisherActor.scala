package actors

import akka.actor.{Actor, ActorLogging}
import cakesolutions.kafka.KafkaProducer.Conf
import cakesolutions.kafka.KafkaProducerRecord
import cakesolutions.kafka.akka.{KafkaProducerActor, ProducerRecords}
import com.google.inject.{Inject, Singleton}
import models.Transaction
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import play.api.Configuration
import play.api.libs.json.Json

@Singleton
class EventPublisherActor @Inject()(configuration: Configuration) extends Actor with ActorLogging {
  private val props = Map(
    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> configuration.getString("kafka.bootstrap-server").getOrElse("localhost:9092"),
    ProducerConfig.CLIENT_ID_CONFIG -> "household-expenses"
  )

  private val config = new Conf(props, new StringSerializer, new StringSerializer)
  private val producer = context.actorOf(KafkaProducerActor.props(config), "producer")

  def receive = {
    case transactions: Seq[Transaction] => publishTransactions(transactions)
  }

  private def publishTransactions(transactions: Seq[Transaction]) = {
    import models.Transaction.JsonProtocol._

    log.info("Importing transactions")

    val records = transactions.map(transaction => KafkaProducerRecord(
      "transactions", transaction.account, Json.toJson(transaction).toString()))

    producer ! ProducerRecords(records)
  }
}
