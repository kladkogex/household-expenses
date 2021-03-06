package actors

import java.util.{Calendar, Date}

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import commands.{GetStateForMonth, ProcessTransaction}

/**
  * Companion object for the monthly summaries
  */
object MonthlySummaries {
  /**
    * Properties for the monthly summaries actor
    * @return Returns the properties for the actor
    */
  def props = Props[MonthlySummaries]
}

/**
  * Provides access to monthly summaries
  */
class MonthlySummaries extends Actor with ActorLogging {
  /**
    * Handles incoming messages
    */
  def receive: Receive = {
    case msg: ProcessTransaction => locateOrCreate(msg.timestamp).forward(msg)
    case msg: GetStateForMonth => locateOrCreate(msg.year, msg.month).forward(msg)
  }

  /**
    * Locates or creates a new monthly summary
    *
    * @param year  Year to find the monthly summary for
    * @param month Month to find the monthly summary for
    * @return Returns the actor reference for the monthly summary
    */
  private def locateOrCreate(year: Int, month: Int): ActorRef = {
    val actorName = s"summary-$year-$month"

    context
      .child(actorName)
      .getOrElse(context.actorOf(MonthlySummary.props(month, year), actorName))
  }

  /**
    * Locates or creates a new monthly summary
    *
    * @param timestamp Timestamp for the transaction
    * @return Returns the actor reference for the monthly summary
    */
  private def locateOrCreate(timestamp: Long): ActorRef = {
    val date = calendar(timestamp)

    val month = date.get(Calendar.MONTH)
    val year = date.get(Calendar.YEAR)

    locateOrCreate(year, month)
  }

  private def calendar(timestamp: Long): Calendar = {
    val calendar = Calendar.getInstance()
    calendar.setTime(new Date(timestamp))

    calendar
  }
}
