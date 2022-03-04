package ru.tinkoff.gatling.feeders

object LuhnValidator {
  val digitalRoot = Seq(0, 0, 1, 2, 2, 4, 3, 6, 4, 8, 5, 1, 6, 3, 7, 5, 8, 7, 9, 9)

  val calculateLuhnSum: String => Int = creditCardNumber => {
    creditCardNumber.map { _.asDigit }.reverse.zipWithIndex.map { case (digit, index) =>
      digitalRoot(digit * 2 + index % 2)
    }.sum
  }

  def validate(creditCardNumber: String): Boolean = {
    calculateLuhnSum(creditCardNumber) % 10 == 0
  }
}
