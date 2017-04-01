package services

import java.nio.charset.Charset
import java.nio.file.{Files, StandardOpenOption}

import helpers.TransactionsFile
import org.scalacheck.{Prop, Properties}
import org.specs2.ScalaCheck
import org.specs2.mutable._

class TransactionsSpec extends Specification with ScalaCheck {
  "Transactions" should {
    "Correctly process files" in {
      Prop.forAll(TransactionsFile.sample) { file =>
        val result = Transactions.parse(file)

        result.isLeft || result.isRight && result.right.get.nonEmpty
      }
    }
  }
}
