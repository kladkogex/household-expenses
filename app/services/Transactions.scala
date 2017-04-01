package services

import java.io.{File, FileReader, IOException}
import java.text.{ParseException, SimpleDateFormat}

import models.Transaction
import org.apache.commons.csv.{CSVFormat, CSVParser}

import scala.collection.JavaConversions._

/**
  * Parses transactions from ING
  */
object Transactions {
  /**
    * Parses the input file into a set of transactions
    *
    * @param input Input file to read from
    * @return Parsed transactions
    */
  def parse(input: File): Either[Throwable, Seq[Transaction]] = {
    try {
      val records = CSVFormat.DEFAULT.withHeader().parse(new FileReader(input))

      if(hasValidFileHeader(records)) {
        Left(new IllegalArgumentException("Specified input file contains invalid header"))
      } else {
        val transactions = records.map(record => Transaction(
          parseDate(record.get(0)), record.get(1), record.get(2), record.get(3),
          record.get(4), record.get(5), parseAmount(record.get(6)), record.get(7), record.get(8)))

        Right(transactions.toSeq)
      }
    }
    catch {
      case e@(_: IOException |
              _: ParseException |
              _: IllegalArgumentException |
              _:ArrayIndexOutOfBoundsException ) => Left(e)
    }
  }

  /**
    * Parses the input string to a timestamp
    *
    * @param value Value to parse
    * @return The timestamp
    */
  private def parseDate(value: String): Long = {
    val format = new SimpleDateFormat("yMd")
    format.parse(value).getTime
  }

  /**
    * Parses the input amount
    *
    * @param value Value to parse
    * @return The amount in euros
    */
  private def parseAmount(value: String): Double = {
    value.replace(",", ".").toDouble
  }

  /**
    * Checks if the CSV parser is for a file of the correct format
    * @param input  Input to parse
    * @return Returns true when the header is valid; Otherwise false.
    */
  private def hasValidFileHeader(input: CSVParser) = {
    val header = input.getHeaderMap

    Seq(
      "Datum",
      "Naam / Omschrijving",
      "Rekening", "Tegenrekening",
      "Code",
      "Af Bij",
      "Bedrag (EUR)",
      "MutatieSoort",
      "Mededelingen"
    ).map(header.containsKey(_)).exists(exists => !exists)
  }
}
