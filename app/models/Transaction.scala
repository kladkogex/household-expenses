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
