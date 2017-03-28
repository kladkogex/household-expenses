package services

import java.io.{File, FileReader, IOException}
import java.text.{ParseException, SimpleDateFormat}

import models.Transaction
import org.apache.commons.csv.CSVFormat

import scala.collection.JavaConversions._

object CSVImporter {
  def parse(input: File): Either[Throwable, Seq[Transaction]] = {
    try {
      val records = CSVFormat.DEFAULT.withHeader().parse(new FileReader(input))

      val transactions = records.map(record => Transaction(
        parseDate(record.get(0)), record.get(1), record.get(2), record.get(3),
        record.get(4), record.get(5), parseAmount(record.get(6)), record.get(7), record.get(8)))

      Right(transactions.toSeq)
    }
    catch {
      case e @ (_:IOException | _:ParseException) => Left(e)
    }
  }

  private def parseDate(value: String): Long = {
    val format = new SimpleDateFormat("yMd")
    format.parse(value).getTime
  }

  private def parseAmount(value: String): Double = {
    value.replace(",", ".").toDouble
  }
}
