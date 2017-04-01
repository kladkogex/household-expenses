package services

import helpers.TransactionsFile
import org.scalacheck.Prop
import org.specs2.ScalaCheck
import org.specs2.mutable._

class TransactionsSpec extends Specification with ScalaCheck {
  val validFiles = Prop.forAll(TransactionsFile.validFile) { file =>
    Transactions.parse(file).isRight
  }

  val filesWithMissingFields = Prop.forAll(TransactionsFile.fileWithMissingFieldInRecords) { file =>
    Transactions.parse(file).isLeft
  }

  val filesWithoutHeader = Prop.forAll(TransactionsFile.fileWithoutHeader) { file =>
    Transactions.parse(file).isLeft
  }

  s2"""
    The transactions module should be able to import files
      - It should process valid files $validFiles
      - It should flag files with missing fields $filesWithMissingFields
      - It should flag files with an incorrect header $filesWithoutHeader
    """
}
