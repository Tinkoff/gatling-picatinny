package ru.tinkoff.gatling.utils

sealed abstract class Brackets(val left: String, val right: String)

object Brackets {
  case object Round  extends Brackets("(", ")")
  case object Square extends Brackets("[", "]")
  case object Curly  extends Brackets("{", "}")
  case object None   extends Brackets("", "")
}
