package helpers

import java.io.File
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.Date

import helpers.TransactionsFile.{fileWithMissingFieldInRecords, fileWithoutHeader}
import org.scalacheck._

import scalaz.Scalaz._

object TransactionsFile {

  val fileHeader = "\"Datum\",\"Naam / Omschrijving\",\"Rekening\",\"Tegenrekening\",\"Code\",\"Af Bij\",\"Bedrag (EUR)\",\"MutatieSoort\",\"Mededelingen\""

  val record: Gen[String] = for {
    date <- Gen.calendar
    description <- Gen.alphaStr
    account <- Gen.oneOf(Seq("NL94INGB0675681219", "NL83INGB0000325513", "NL36INGB0003445588"))
    contraAccount <- Gen.oneOf("NL36INGB0003445588", "NL83INGB0000325513", "NL36INGB0003445588", "")
    code <- Gen.oneOf("BA", "IC", "GM", "GT")
    direction <- Gen.oneOf("Bij", "Af")
    amount <- Gen.oneOf(Gen.posNum[Double], Gen.negNum[Double])
    mutationKind <- Gen.alphaStr
    comments <- Gen.alphaStr
  } yield Seq(
    formatDate(date.getTime),
    description,
    account,
    contraAccount,
    code,
    direction,
    amount,
    mutationKind,
    comments) |> csvRecord

  val recordWithMissingField: Gen[String] = for {
    date <- Gen.calendar
    description <- Gen.alphaStr
    account <- Gen.oneOf(Seq("NL94INGB0675681219", "NL83INGB0000325513", "NL36INGB0003445588"))
    contraAccount <- Gen.oneOf("NL36INGB0003445588", "NL83INGB0000325513", "NL36INGB0003445588", "")
    code <- Gen.oneOf("BA", "IC", "GM", "GT")
    direction <- Gen.oneOf("Bij", "Af")
    amount <- Gen.oneOf(Gen.posNum[Double], Gen.negNum[Double])
    mutationKind <- Gen.alphaStr
    comments <- Gen.alphaStr
    missingFieldIndex <- Gen.choose(0, 7)
  } yield Seq(
    formatDate(date.getTime),
    description,
    account,
    contraAccount,
    code,
    direction,
    amount,
    mutationKind,
    comments).patch(missingFieldIndex, Nil, 1) |> csvRecord

  val validFile: Gen[File] = for {
    records <- Gen.listOf(record)
    if records.nonEmpty
  } yield fileContent(fileHeader :: records) |> temporaryFile

  val fileWithoutHeader: Gen[File] = for {
    records <- Gen.listOf(record)
    if records.nonEmpty
  } yield fileContent(records) |> temporaryFile

  val fileWithMissingFieldInRecords: Gen[File] = for {
    records <- Gen.listOf(recordWithMissingField)
    if records.nonEmpty
  } yield fileContent(fileHeader :: records) |> temporaryFile

  val sample: Gen[File] = Gen.oneOf(validFile, fileWithoutHeader, fileWithMissingFieldInRecords)

  private def csvRecord[T](fields: Seq[T]): String = fields.map(field => "\"" + field + "\"").reduce(_ + "," + _)

  private def temporaryFile(content: String) = Files.write(Files.createTempFile("import", ".csv"), content.getBytes).toFile

  private def fileContent(content: Seq[String]) = if(content.isEmpty) {
    ""
  } else content.reduce(_ + "\r\n" + _)

  private def formatDate(date: Date) = {
    val format = new SimpleDateFormat("yyyyMMdd")
    format.format(date)
  }
}
