package actors

import java.io.File

import akka.actor.{Actor, ActorLogging, Props}
import models.Transaction
import services.Transactions

/**
  * Companion object for the transaction importer
  */
object TransactionImporter {

  /**
    * An import failure
    *
    * @param message Message explaining why the import failed
    */
  case class ImportFailure(message: String)

  /**
    * The import results
    *
    * @param numberOfTransactions The number of transactions imported
    */
  case class ImportResults(numberOfTransactions: Long)

  /**
    * Defines the properties for the transaction importer actor
    *
    * @return
    */
  def props = Props(classOf[TransactionImporter])
}

/**
  * Actor responsible for importing transactions posted by the user
  */
class TransactionImporter extends Actor with ActorLogging {

  import TransactionImporter._

  /**
    * Handles incoming messages
    *
    * @return
    */
  def receive: PartialFunction[Any, Unit] = {
    case input: File => parseFile(input)
  }

  /**
    * Parses the input file
    *
    * @param input Input file to parse
    */
  private def parseFile(input: File) = {
    Transactions.parse(input) match {
      case Left(error) => sender ! ImportFailure(error.getMessage)
      case Right(transactions) => publishTransactions(transactions)
    }
  }

  /**
    * Publishes the imported transactions
    *
    * @param transactions Transactions that were imported
    */
  private def publishTransactions(transactions: Seq[Transaction]) = {
    sender ! ImportResults(transactions.size)
  }
}
