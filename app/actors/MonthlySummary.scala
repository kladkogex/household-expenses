package actors


import akka.actor.{ActorLogging, Props}
import akka.persistence.{PersistentActor, SnapshotOffer}
import commands.{GetStateForMonth, ProcessTransaction}
import events.TransactionReceived

/**
  * Companion object for the monthly summary
  */
object MonthlySummary {

  /**
    * The internal state for the monthly summary
    *
    * @param expenses The expenses per category
    * @param total    The total amount spend
    */
  case class MonthlySummaryState(expenses: Map[String, Double], total: Double) {
    /**
      * Updates the state for the monthly summary
      *
      * @param category Category for the transaction
      * @param amount   Amount transferred
      * @return Returns the updated state
      */
    def updated(category: String, amount: Double) = {
      MonthlySummaryState(expenses.updated(category,
        expenses.get(category).map(_ + amount).getOrElse(amount)), total + amount)
    }
  }

  /**
    * Defines the properties for the actor
    *
    * @param month Month for which to keep track of the expenses
    * @param year  Year for which to keep track of expenses
    * @return The properties for the actor
    */
  def props(month: Int, year: Int) = Props(classOf[MonthlySummary], month, year)
}

/**
  * Keeps track of the overview of a single month
  *
  * @param month Month for which to keep track of the expenses
  * @param year  Year for which to keep track of the expenses
  */
class MonthlySummary(month: Int, year: Int) extends PersistentActor with ActorLogging {

  import actors.MonthlySummary._

  private var state = MonthlySummaryState(Map(), 0.0)

  /**
    * Persistence ID for the actor
    */
  override def persistenceId: String = s"$year-$month"

  /**
    * Handles the recovery of previously stored transactions and snapshot information
    */
  override def receiveRecover: Receive = {
    case evt: TransactionReceived => updateState(evt)
    case SnapshotOffer(_, snapshotState: MonthlySummaryState) => restoreState(snapshotState)
  }

  /**
    * Handles incoming commands for the persistent actor
    */
  override def receiveCommand: Receive = {
    case ProcessTransaction(timestamp, category, amount) => persist(TransactionReceived(category, amount))(updateState)
    case GetStateForMonth(_, _) => sender ! state
  }

  /**
    * Updates the state of the actor based on the incoming event
    *
    * @param evt Event to process
    */
  private def updateState(evt: TransactionReceived) = {
    state = state.updated(evt.category, evt.amount)

    log.info(s"Received transaction for amount ${evt.amount} for category ${evt.category}")
    log.info(s"Total for month ${state.total}")
    log.info(s"Total for category ${state.expenses(evt.category)}")
  }

  /**
    * Restores the state of the actor
    * @param snapshotState  State snapshot
    */
  private def restoreState(snapshotState: MonthlySummaryState): Unit = {
    state = snapshotState
  }
}
