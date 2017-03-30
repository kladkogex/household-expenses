package actors

import java.io.File

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import models.Transaction
import services.CSVImporter

object TransactionImporterActor {

  case class ImportFailure(message: String)
  case class ImportResults(numberOfTransactions: Long)

  def props(eventPublisher: ActorRef) = Props(classOf[TransactionImporterActor], eventPublisher)
}

class TransactionImporterActor(publisher: ActorRef) extends Actor with ActorLogging {

  import TransactionImporterActor._

  def receive: PartialFunction[Any, Unit] = {
    case input: File => parseFile(input)
  }

  private def parseFile(input: File) = {
    CSVImporter.parse(input) match {
      case Left(error) => sender ! ImportFailure(error.getMessage)
      case Right(transactions) => publishTransactions(transactions)
    }
  }

  private def publishTransactions(transactions: Seq[Transaction]) = {
    publisher ! transactions
    sender ! ImportResults(transactions.size)
  }
}
