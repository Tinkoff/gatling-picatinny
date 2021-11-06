package ru.tinkoff.gatling.utils

import ru.tinkoff.gatling.utils.RandomDataGenerators.digitString

object RandomPhoneGenerator {

  // Default value for genRegionCode if digitString() generates "000"
  final val DEFAULT_REGION_CODE = "123"

  def randomPhone(
      countryCode: String,
      regionCode: Option[String],
      delimiter: String,
      brackets: Brackets,
  ): String = {

    def genRegionCode: String = {
      val rc = regionCode.getOrElse(digitString(3))
      if (rc != "000") rc else DEFAULT_REGION_CODE
    }

    s"""$countryCode${brackets.left}${genRegionCode}${brackets.right}${digitString(3)}$delimiter${digitString(2)}$delimiter${digitString(2)}"""
  }

}
