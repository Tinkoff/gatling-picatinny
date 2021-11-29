package ru.tinkoff.gatling.utils

import java.time.{Instant, LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalUnit
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import com.eatthepath.uuid.FastUUID
import ru.tinkoff.gatling.utils.RandomDigitMagnet.DigitMagnet

import scala.annotation.tailrec
import scala.util.Random

object RandomDataGenerators {

  val random: Random.type = scala.util.Random

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

  def randomLettersString(stringLength: Int): String = {
    require(stringLength>0, s"randomString generator required string length input >0. Current value = $stringLength")
    Random.alphanumeric.dropWhile(_.isDigit).take(stringLength).mkString
  }

  def randomCyrillicString(n: Int): String =
    randomString("АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя")(n)

  def randomPhone(countryCode: String = "+7"): String = s"""$countryCode${this.digitString(10)}"""

  def randomDigit(): Int = ThreadLocalRandom.current().nextInt()

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

  def getRandomElement[T](items: List[T], random: Random): T = items(random.nextInt(items.length))

  def getYear: String = LocalDateTime.now().getYear.toString

  /** Random PAN generation method.
    *
    * This method generates a random PAN (Primary Account Number).
    *
    * @param bin - Bank Identification Number (BIN) refers to the first six numbers on a payment card.
    *             This set of numbers identifies the financial institution that issues the card.
    * @return random string PAN
    */
  def randomPAN(bin: List[String] = List.empty[String]): String = {
    val r: Random = new Random()
    val result: List[Char] = s"""${this.getRandomElement(bin, r)}${this.digitString(9)}""".toList
    val even: Int = result.zipWithIndex.filter(_._2 % 2 == 1).map(_._1).flatMap(_.toString.toIntOption).sum
    val oddList: List[Int] = result.zipWithIndex.filter(_._2 % 2 == 0).map(_._1).flatMap(_.toString.toIntOption)

    @tailrec
    def iterateOdd(source: List[Int], destination:List[Int]): List[Int] = source match {
      case el::tail => iterateOdd(tail, destination :+ (if (el * 2 > 9) el * 2 - 9 else el * 2))
      case _ => destination
    }

    val odd: Int = iterateOdd(oddList, List()).sum
    val control: Int = 10 - (even + odd) % 10

    s"""${result.mkString("")}$control"""
  }

  /** Random OGRN generation method.
    *
    * This method generates a random OGRN (Primary State Registration Number).
    * OGRN is used only in the Russian Federation.
    *
    * @return random string OGRN
    */
  def randomOGRN(): String = {
    val r: Random = new Random()
    val result: String = s"""${this.getRandomElement(List(1, 5), r)}${this.getYear.slice(2, 4)}${String.format("%02d", r.between(1, 90))}${this.digitString(7)}"""
    val rem: Long = result.toLong % 11

    if (rem == 10)
      result + "0"
    else {
      result + rem.toString
    }
  }

  /** Random PSRNSP generation method.
    *
    * This method generates a random PSRNSP (Primary State Registration Number of the Sole Proprietor).
    * PSRNSP is used only in the Russian Federation.
    *
    * @return random string PSRNSP
    */
  def randomPSRNSP(): String = {
    val r: Random = new Random()
    val result: String = s"""${this.getRandomElement(List(1, 5), r)}${this.getYear.slice(2, 4)}${String.format("%02d", r.between(1, 90))}${this.digitString(9)}"""
    val rem: Long = result.toLong % 13

    if (rem == 10)
      result + "0"
    else {
      result + rem.toString
    }
  }

  /** Random KPP generation method.
    *
    * This method generates a random KPP (Tax Registration Reason Code).
    * KPP is used only in the Russian Federation.
    *
    * @return random string KPP
    */
  def randomKPP(): String =
    s"""${String.format("%04d", random.between(1, 10000))}${String.format("%02d", random.between(1, 100))}${String.format("%03d", random.between(1, 1000))}"""


  /** Random ITN of the natural person generation method.
    *
    * This method generates a random ITN of the natural person (Individual Taxpayer Number).
    * ITN is used only in the Russian Federation.
    *
    * @return random string ITN of the natural person
    */
  def randomNatITN(): String = {

    @tailrec
    def itnNatRecursion(n: Int, sum: Int, result: List[Int]): String = {
      val r: Random = new Random()
      val rnd: Int = r.nextInt(10)
      val num: List[Int] = List(2, 4, 10, 3, 5, 9, 4, 6, 8)

      def checkSum: Int = sum + rnd * num(9 - n)

      n match {
        case 1 => (result :+ rnd :+ checkSum % 11).mkString("")
        case _ => itnNatRecursion(n - 1, checkSum, result :+ rnd)
      }
    }

    itnNatRecursion(9, 0, List.empty[Int])
  }

  /** Random ITN of the juridical person generation method.
    *
    * This method generates a random ITN of the juridical person (Individual Taxpayer Number).
    * ITN is used only in the Russian Federation.
    *
    * @return random string ITN of the juridical person
    */
  def randomJurITN(): String = {

    @tailrec
    def itnJurRecursion(n: Int, sum1: Int, sum2: Int, result: List[Int]): String = {
      val r: Random = new Random()
      val rnd: Int = r.nextInt(10)
      val num1: List[Int] = List(7, 2, 4, 10, 3, 5, 9, 4, 6, 8)
      val num2: List[Int] = List(3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8)

      def checkSum1: Int = sum1 + rnd * num1(11 - n)

      def checkSum2: Int = sum2 + rnd * num2(11 - n)

      n match {
        case 1 => (result :+ (if ((sum2 + result.last * num2(11 - n)) % 11 == 10) 0 else (sum2 + result.last * num2(11 - n)) % 11)).mkString("")
        case 2 => itnJurRecursion(n - 1, checkSum1, checkSum2, result :+ rnd :+ (if (checkSum1 % 11 == 10) 0 else checkSum1 % 11))
        case _ => itnJurRecursion(n - 1, checkSum1, checkSum2, result :+ rnd)
      }
    }

    itnJurRecursion(11, 0, 0, List.empty[Int])
  }

  /** Random SNILS generation method.
    *
    * This method generates a random SNILS (Insurance Number of Individual Ledger Account).
    * SNILS is used only in the Russian Federation.
    *
    * @return random string SNILS
    */
  def randomSNILS(): String = {

    @tailrec
    def snilsRecursion(n: Int, sum: Int, result: List[Int]): String = {
      val r: Random = new Random()
      val rnd: Int = r.nextInt(10)

      def checkSum = sum + rnd * n

      n match {
        case 1 => (result :+ rnd :+ checkSum % 101).mkString("")
        case _ => snilsRecursion(n - 1, checkSum, result :+ rnd)
      }
    }

    snilsRecursion(9, 0, List.empty[Int])
  }

  /** Random russian passport series and number generation method.
    *
    * This method generates a random russian passport series and number.
    *
    * @return random string russian passport series and number.
    */
  def randomRusPassport(): String = {
    val r: Random = new Random()
    s"""${String.format("%02d", r.between(1, 90))}${getYear.slice(2, 4)}${digitString(6)}"""
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
