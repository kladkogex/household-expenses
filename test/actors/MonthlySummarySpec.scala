package actors

import java.util.Date

import akka.testkit.TestProbe
import commands.{GetStateForMonth, ProcessTransaction}
import helpers.AkkaSpec
import org.specs2.mutable.Specification

import scala.concurrent.duration._
import scala.math.BigDecimal.RoundingMode

class MonthlySummarySpec extends Specification {
  "Monthly summary" should {
    "correctly calculate the expenses of a given month" in new AkkaSpec {
      val monthlySummary = system.actorOf(MonthlySummary.props(1, 2017))
      val sender = TestProbe("test-sender")

      var totalAmount = round(119.10 + 34.5 + 2.95, 2)
      var categorized = Map(
        "Groceries" -> round(2.95 + 34.5, 2),
        "Insurance" -> 119.10
      )

      monthlySummary ! ProcessTransaction(new Date().getTime, "Groceries", 2.95)
      monthlySummary ! ProcessTransaction(new Date().getTime, "Groceries", 34.5)
      monthlySummary ! ProcessTransaction(new Date().getTime, "Insurance", 119.10)

      monthlySummary.tell(GetStateForMonth(2017, 1), sender.ref)

      sender.expectMsg(200 millis, MonthlySummary.MonthlySummaryState(categorized, totalAmount))
    }
  }

  private def round(value: Double, decimals: Int) = BigDecimal(value).setScale(decimals, RoundingMode.HALF_UP).toDouble
}
