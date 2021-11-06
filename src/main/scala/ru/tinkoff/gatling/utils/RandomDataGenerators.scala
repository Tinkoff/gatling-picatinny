package ru.tinkoff.gatling.utils

import com.eatthepath.uuid.FastUUID
import ru.tinkoff.gatling.utils.RandomDigitMagnet.DigitMagnet
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalUnit
import java.time.{Instant, LocalDateTime, ZoneId}
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import scala.annotation.tailrec
import scala.util.Random

object RandomDataGenerators {

  def randomString(alphabet: String)(n: Int): String = {
    require(alphabet.nonEmpty, "randomString generator required non empty alphabet input")
    require(n > 0, s"randomString generator required string length input >0. Current value = $n")
    Iterator.continually(Random.nextInt(alphabet.length)).map(alphabet).take(n).mkString
  }

  def digitString(n: Int): String =
    randomString("0123456789")(n)

  def hexString(n: Int): String =
    randomString("0123456789abcdef")(n)

  def alphanumericString(stringLength: Int): String = {
    require(stringLength > 0, s"randomString generator required string length input >0. Current value = $stringLength")
    Random.alphanumeric.take(stringLength).mkString
  }

  def randomOnlyLettersString(stringLength: Int): String = {
    require(stringLength > 0, s"randomString generator required string length input >0. Current value = $stringLength")
    Random.alphanumeric.dropWhile(_.isDigit).take(stringLength).mkString
  }

  def randomCyrillicString(n: Int): String =
    randomString("АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя")(n)

  def randomDigit(): Int = ThreadLocalRandom.current().nextInt()

  def randomDigit(size: Int): Int = ThreadLocalRandom.current().nextInt(size)

  def randomDigit(min: Int, max: Int): Int = {
    require(min < max)
    ThreadLocalRandom.current().nextInt(min, max)
  }

  def randomDigit(min: Long, max: Long): Long = {
    require(min < max)
    ThreadLocalRandom.current().nextLong(min, max)
  }

  def randomDigit(magnet: DigitMagnet): magnet.Result = magnet.RandomImpl

  def randomUUID: String = FastUUID.toString(UUID.randomUUID)

  private def getRandomElement(items: List[Int], intLength: Int): Int = items match {
    case Nil => randomDigit(intLength)
    case _   => items(randomDigit(items.length))
  }

  private def getRandomElement(items: List[String], stringLength: Int): String = items match {
    case Nil => randomOnlyLettersString(stringLength)
    case _   => items(randomDigit(items.length))
  }

  def randomPAN(bins: String*): String = {
    val idNum: String = digitString(9)

    def fifteenDigits(bins: List[String]): List[Char] = bins match {
      case Nil => s"""${digitString(6)}$idNum""".toList
      case _   => s"""${getRandomElement(bins, 6)}$idNum""".toList
    }

    val results: List[Int] = fifteenDigits(bins.toList).flatMap(_.toString.toIntOption)
    val evenPosSum: Int    = results.indices.collect {
      case i if i % 2 == 0 => results(i)
    }.fold(0)((x, y) => x + (if (y * 2 > 9) y * 2 - 9 else y * 2))
    val oddPosSum: Int     = results.indices.collect { case i if i % 2 != 0 => results(i) }.sum
    val controlNum: Int    = 10 - (oddPosSum + evenPosSum) % 10

    s"""${results.mkString("")}$controlNum"""
  }

  def randomOGRN(): String = {
    val indicatorOGRN: Int   = getRandomElement(List(1, 5), 1)
    val year: String         = String.format("%02d", randomDigit(2, 21))
    val ruSubjectNum: String = String.format("%02d", randomDigit(1, 90))
    val idNum: String        = digitString(7)
    val result: String       = s"""$indicatorOGRN$year$ruSubjectNum$idNum"""
    val rem: Long            = result.toLong % 11

    if (rem == 10)
      s"""${result}0"""
    else
      s"""$result$rem"""
  }

  def randomPSRNSP(): String = {
    val indicatorPSRNSP: Int = 3
    val year: String         = String.format("%02d", randomDigit(2, 21))
    val ruSubjectNum: String = String.format("%02d", randomDigit(1, 90))
    val idNum: String        = digitString(9)
    val result: String       = s"""$indicatorPSRNSP$year$ruSubjectNum$idNum"""
    val rem: Long            = result.toLong % 13 % 10

    if (rem == 10)
      s"""${result}0"""
    else
      s"""$result$rem"""
  }

  def randomKPP(): String = {
    val revenueServiceCode: String = String.format("%04d", randomDigit(1, 10000))
    val reasonForReg: String       = String.format("%02d", randomDigit(1, 100))
    val idNum: String              = String.format("%03d", randomDigit(1, 1000))

    s"""$revenueServiceCode$reasonForReg$idNum"""
  }

  def randomNatITN(): String = {

    @tailrec
    def itnNatRecursion(n: Int, sum: Int, results: List[Int]): String = {
      val rnd: Int           = randomDigit(0, 10)
      val factors: List[Int] = List(2, 4, 10, 3, 5, 9, 4, 6, 8)

      def checkSum: Int = sum + rnd * factors(9 - n)

      n match {
        case 1 => (results :+ rnd :+ (if (checkSum % 11 == 10) 0 else checkSum % 11)).mkString("")
        case _ => itnNatRecursion(n - 1, checkSum, results :+ rnd)
      }
    }

    itnNatRecursion(9, 0, List.empty[Int])
  }

  def randomJurITN(): String = {

    @tailrec
    def itnJurRecursion(n: Int, sum1: Int, sum2: Int, results: List[Int]): String = {
      val rnd: Int                 = randomDigit(0, 10)
      val firstFactors: List[Int]  = List(7, 2, 4, 10, 3, 5, 9, 4, 6, 8)
      val secondFactors: List[Int] = List(3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8)

      def checkSum1: Int = sum1 + rnd * firstFactors(11 - n)

      def checkSum2: Int = sum2 + rnd * secondFactors(11 - n)

      n match {
        case 1 =>
          (results :+ (if ((sum2 + results.last * secondFactors(11 - n)) % 11 == 10) 0
                       else
                         (sum2 + results.last * secondFactors(11 - n))   % 11)).mkString("")
        case 2 =>
          itnJurRecursion(
            n - 1,
            checkSum1,
            checkSum2,
            results :+ rnd :+ (if (checkSum1 % 11 == 10) 0
                               else
                                 checkSum1   % 11),
          )
        case _ => itnJurRecursion(n - 1, checkSum1, checkSum2, results :+ rnd)
      }
    }

    itnJurRecursion(11, 0, 0, List.empty[Int])
  }

  def randomSNILS(): String = {

    @tailrec
    def snilsRecursion(n: Int, sum: Int, results: List[Int]): String = {
      val rnd: Int = randomDigit(0, 10)

      def checkSum: Int = sum + rnd * n

      @tailrec
      def defineCheckSum(checkSum: Int): String = checkSum match {
        case x if x < 101 => checkSum.toString
        case 100 | 101    => "00"
        case _            => defineCheckSum(checkSum % 101)
      }

      n match {
        case 1 => (results :+ rnd :+ defineCheckSum(checkSum)).mkString("")
        case _ => snilsRecursion(n - 1, checkSum, results :+ rnd)
      }
    }

    snilsRecursion(9, 0, List.empty[Int])
  }

  def randomRusPassport(): String = {
    val ruSubjectNum: String = String.format("%02d", randomDigit(1, 90))
    val year: String         = String.format("%02d", randomDigit(0, 21))
    val idNum: String        = digitString(6)

    s"""$ruSubjectNum$year$idNum"""
  }

  /** Pattern examples: yyyy.MM.dd G 'at' HH:mm:ss z 2001.07.04 AD at 12:08:56 PDT EEE, MMM d, ''yy Wed, Jul 4, '01 h:mm a 12:08
    * PM hh 'o''clock' a, zzzz 12 o'clock PM, Pacific Daylight Time K:mm a, z 0:08 PM, PDT yyyyy.MMMMM.dd GGG hh:mm aaa
    * 02001.July.04 AD 12:08 PM EEE, d MMM yyyy HH:mm:ss Z Wed, 4 Jul 2001 12:08:56 -0700 yyMMddHHmmssZ 010704120856-0700
    * yyyy-MM-dd'T'HH:mm:ss.SSSZ 2001-07-04T12:08:56.235-0700 yyyy-MM-dd'T'HH:mm:ss.SSSXXX 2001-07-04T12:08:56.235-07:00
    * YYYY-'W'ww-u 2001-W27-3
    */
  def randomDate(
      positiveDelta: Int,
      negativeDelta: Int,
      datePattern: String,
      dateFrom: LocalDateTime,
      unit: TemporalUnit,
      timezone: ZoneId,
  ): String = {
    require(
      positiveDelta >= 0 && negativeDelta >= 0,
      s"RandomDateFeeder delta requires values >0. Current values: positiveDelta= $positiveDelta, negativeDelta= $negativeDelta",
    )
    dateFrom
      .plus(randomDigit(-negativeDelta, positiveDelta), unit)
      .atZone(timezone)
      .format(DateTimeFormatter.ofPattern(datePattern))
  }

  def randomDate(
      offsetDate: Long,
      datePattern: String = "yyyy-MM-dd",
      dateFrom: LocalDateTime,
      unit: TemporalUnit,
      timezone: ZoneId,
  ): String = {
    require(offsetDate > 1, s"RandomRangeDateFeeder offset requires value >1. Current values: offsetDate= $offsetDate")
    dateFrom.plus(randomDigit(1, offsetDate), unit).atZone(timezone).format(DateTimeFormatter.ofPattern(datePattern))
  }

  def currentDate(datePattern: DateTimeFormatter, timezone: ZoneId): String = {
    Instant.now.atZone(timezone).format(datePattern)
  }

}
