package actors

import java.io.File

import akka.actor.{Actor, ActorLogging, Props}
import models.Transaction
import services.CSVImporter

object TransactionImporterActor {

  case class ImportFailure(message: String)
  case class ImportResults(numberOfTransactions: Long)

  def props = Props(classOf[TransactionImporterActor])
}

class TransactionImporterActor extends Actor with ActorLogging {

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
    //TODO: Publish the transactions on the event bus
    sender ! ImportResults(transactions.size)
  }
}
