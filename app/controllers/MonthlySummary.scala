package controllers

import java.util.{Calendar, Locale}

import actors.MonthlySummaries
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.{Inject, Singleton}
import commands.GetStateForMonth
import play.api.mvc.{Action, AnyContent, Controller}

import scala.collection.JavaConversions._
import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Controller for the monthly summaries
  */
@Singleton
class MonthlySummary @Inject()(actorSystem: ActorSystem) extends Controller {

  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  private val summaries = actorSystem.actorOf(MonthlySummaries.props)

  implicit val timeout: Timeout = 500.millis

  def index: Action[AnyContent] = Action.async { implicit request =>
    (summaries ? GetStateForMonth(currentYear, currentMonth))
      .mapTo[actors.MonthlySummary.MonthlySummaryState]
      .flatMap(result => Future {
        Ok(views.html.summaries.month(currentYear, monthName(currentMonth), result.expenses, result.total))
      })
  }

  private def monthName(month: Int) = {
    val calendar = Calendar.getInstance()
    val monthNames = calendar.getDisplayNames(Calendar.MONTH, Calendar.LONG, new Locale("nl"))

    monthNames.entrySet()
      .map(entry => (entry.getValue, entry.getKey))
      .toSeq.filter(entry => entry._1 == month).minBy {
        case (index, name) => index
      }._2
  }

  private def currentMonth = {
    Calendar.getInstance().get(Calendar.MONTH)
  }

  private def currentYear = {
    Calendar.getInstance().get(Calendar.YEAR)
  }
}