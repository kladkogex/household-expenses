package models

case class Transaction(date: Long,
                       description: String,
                       account: String,
                       counterAccount: String,
                       code: String,
                       direction: String,
                       amount: Double,
                       mutationKind: String,
                       comments: String)

object Transaction {
  object JsonProtocol {
    import play.api.libs.json._
    import play.api.libs.functional.syntax._

    implicit val transactionWrites = new Writes[Transaction] {
      override def writes(o: Transaction): JsValue = Json.obj(
        "date" -> o.date,
        "description" -> o.description,
        "account" -> o.account,
        "counterAccount" -> o.counterAccount,
        "code" -> o.code,
        "direction" -> o.direction,
        "amount" -> o.amount,
        "mutationKind" -> o.mutationKind,
        "comments" -> o.comments
      )
    }
  }
}