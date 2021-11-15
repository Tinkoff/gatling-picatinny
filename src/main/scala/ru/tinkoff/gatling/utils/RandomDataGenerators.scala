package ru.tinkoff.gatling.utils

import java.time.{Instant, LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalUnit
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import com.eatthepath.uuid.FastUUID
import ru.tinkoff.gatling.utils.RandomDigitMagnet.DigitMagnet
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

  def randomPAN(status: String = getRandomElement(Seq('P', 'C', 'H', 'A', 'B', 'G', 'J', 'L', 'F', 'I'), new Random()).toString,
                name: String = randomLettersString(1)): String =
    (randomLettersString(3) + status + name + digitString(4) + randomLettersString(1)).toUpperCase

  def getRandomElement[T](seq: Seq[T], random: Random): T =
    seq(random.nextInt(seq.length))

  def randomOGRN(owner: String = getRandomElement(Seq(1, 5), new Random()).toString,
                 date: String = LocalDateTime.now().getYear.toString.slice(2, 4),
                 reg: String = scala.util.Random.between(1, 90).toString): String = {
    val number: String =
      if (owner != "1" || owner != "5")
        getRandomElement(Seq(1, 5), new Random()).toString + date + reg + String.format("%07d", scala.util.Random.between(1, 10000000))
      else
        owner + date + reg + String.format("%07d", scala.util.Random.between(1, 10000000))
    if (number.toLong % 11 == 10)
      number + '0'
    else
      number + (number.toLong % 11).toString
  }

  def randomINN(isPhysPers: Boolean = true,
                reg: Int = scala.util.Random.between(1, 90),
                number: Int = scala.util.Random.between(1, 100)): String = {
    def check(result: Int): String = {
      if (result > 9)
        (result % 10).toString
      else
        result.toString
    }
    var inn: String = String.format("%02d", reg) + String.format("%02d", number)
    if (isPhysPers) {
      inn += String.format("%05d", scala.util.Random.between(1, 100000))
      val control: Array[Int] = inn.split("").map(_.toInt)
      val result: Int = (control(0) * 2 + control(1) * 4 + control(2) * 10 + control(3) * 3 + control(4) * 5 +
        control(5) * 9 + control(6) * 4 + control(7) * 6 + control(8) * 8) % 11
      inn + check(result)
    }
    else {
      inn += String.format("%06d", scala.util.Random.between(1, 1000000))
      val control_1: Array[Int] = inn.split("").map(_.toInt)
      val result_1: Int = (control_1(0) * 7 + control_1(1) * 2 + control_1(2) * 4 + control_1(3) * 10 + control_1(4) * 3 +
        control_1(5) * 5 + control_1(6) * 9 + control_1(7) * 4 + control_1(8) * 6 + control_1(9) * 8) % 11
      inn += check(result_1)
      val control_2: Array[Int] = inn.split("").map(_.toInt)
      val result_2: Int = (control_2(0) * 3 + control_2(1) * 7 + control_2(2) * 2 + control_2(3) * 4 + control_2(4) * 10 +
        control_2(5) * 3 + control_2(6) * 5 + control_2(7) * 9 + control_2(8) * 4 + control_2(9) * 6 + control_2(10) * 8) % 11
      inn + check(result_2)
    }
  }

  def randomSNILS(): String = {
    val snilsBefore: String = String.format("%09d", scala.util.Random.between(1, 1000000000))
    val control: Array[Int] = snilsBefore.split("").map(_.toInt)
    val result: Int = control(0) * 9 + control(1) * 8 + control(2) * 7 + control(3) * 6 + control(4) * 5 +
      control(5) * 4 + control(6) * 3 + control(7) * 2 + control(8) * 1
    if (result < 100)
      snilsBefore.slice(0, 3) + '-' + snilsBefore.slice(3, 6) + '-' + snilsBefore.slice(6, 9) + ' ' + result.toString
    else
      snilsBefore.slice(0, 3) + '-' + snilsBefore.slice(3, 6) + '-' + snilsBefore.slice(6, 9) + ' ' + "00"
  }

  def randomPassport(region: String = scala.util.Random.between(1, 90).toString,
                     date: String = LocalDateTime.now().getYear.toString.slice(2, 4)): String =
    region + date + " " + String.format("%06d", scala.util.Random.between(1, 1000000))

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
