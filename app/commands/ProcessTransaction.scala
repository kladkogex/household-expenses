package commands

import models.Transaction

case class ProcessTransaction(timestamp:Long, category: String, amount: Double)
