package ru.tinkoff.gatling.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalUnit
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicLong

import com.eatthepath.uuid.FastUUID
import ru.tinkoff.gatling.utils.RandomDigitMagnet.DigitMagnet

import scala.collection.immutable.Stream
import scala.util.Random

private[gatling] object RandomDataGenerators {

  def randomString(alphabet: String)(n: Int): String = {
    require(alphabet.nonEmpty, "randomString generator required non empty alphabet input")
    require(n>0, s"randomString generator required string length input >0. Current value = $n")
    Stream.continually(Random.nextInt(alphabet.length)).map(alphabet).take(n).mkString
  }

  def digitString(n: Int): String =
    randomString("0123456789")(n)

  def hexString(n: Int): String =
    randomString("0123456789abcdef")(n)

  def alphanumericString(stringLength: Int): String = {
    require(stringLength>0, s"randomString generator required string length input >0. Current value = $stringLength")
    Random.alphanumeric.take(stringLength).mkString
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

  /**
  Pattern examples:
   yyyy.MM.dd G 'at' HH:mm:ss z 	2001.07.04 AD at 12:08:56 PDT
   EEE, MMM d, ''yy	            Wed, Jul 4, '01
   h:mm a                      	12:08 PM
   hh 'o''clock' a, zzzz	        12 o'clock PM, Pacific Daylight Time
   K:mm a, z	                    0:08 PM, PDT
   yyyyy.MMMMM.dd GGG hh:mm aaa 	02001.July.04 AD 12:08 PM
   EEE, d MMM yyyy HH:mm:ss Z   	Wed, 4 Jul 2001 12:08:56 -0700
   yyMMddHHmmssZ               	010704120856-0700
   yyyy-MM-dd'T'HH:mm:ss.SSSZ  	2001-07-04T12:08:56.235-0700
   yyyy-MM-dd'T'HH:mm:ss.SSSXXX 	2001-07-04T12:08:56.235-07:00
   YYYY-'W'ww-u                  2001-W27-3
    */
  def randomDate(positiveDelta: Int,
                 negativeDelta: Int,
                 datePattern: String,
                 dateFrom: LocalDateTime,
                 unit: TemporalUnit): String = {
    require(
      positiveDelta >= 0 && negativeDelta >= 0,
      s"RandomDateFeeder delta requires values >0. Current values: positiveDelta= $positiveDelta, negativeDelta= $negativeDelta"
    )
    dateFrom.plus(randomDigit(-negativeDelta, positiveDelta), unit).format(DateTimeFormatter.ofPattern(datePattern))
  }

  def randomDate(offsetDate: Long, datePattern: String = "yyyy-MM-dd", dateFrom: LocalDateTime, unit: TemporalUnit): String = {
    require(offsetDate > 1, s"RandomRangeDateFeeder offset requires value >1. Current values: offsetDate= $offsetDate")
    dateFrom.plus(randomDigit(1, offsetDate), unit).format(DateTimeFormatter.ofPattern(datePattern))
  }

}
