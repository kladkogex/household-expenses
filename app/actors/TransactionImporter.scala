package actors

import java.io.File

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.google.inject.Inject
import com.google.inject.name.Named
import commands.ProcessTransaction
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
class TransactionImporter @Inject() (@Named("monthly-summaries")monthlySummaries: ActorRef) extends Actor with ActorLogging {

  import TransactionImporter._

  /**
    * Handles incoming messages
    */
  def receive: Receive = {
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
    for(tx <- transactions) {
      monthlySummaries ! ProcessTransaction(tx.date, "Overige", tx.amount)
    }

    sender ! ImportResults(transactions.size)
  }
}
