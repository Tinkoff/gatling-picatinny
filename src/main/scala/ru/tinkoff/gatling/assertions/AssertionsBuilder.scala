package assertions

import pureconfig.module.yaml._
import io.gatling.core.Predef._

import io.gatling.core.scenario.Simulation
import pureconfig.generic.auto._

object AssertionsBuilder extends Simulation {

  case class innerConf(key: String, value: Map[String,String])
  case class nfr(nfr: List[innerConf])

  def getNfr(path: String) =
    YamlConfigSource.file(path).asObjectSource.loadOrThrow[nfr]

  def toUtf(baseString:String): String =
    scala.io.Source.fromBytes(baseString.getBytes(), "UTF-8").mkString


  def assertionFromYaml(path:String):Iterable[Assertion] ={
    val NFR = getNfr(path).nfr
    val assertList:scala.collection.mutable.ListBuffer[Assertion] = scala.collection.mutable.ListBuffer.empty[Assertion]

    for(i <- 0 to NFR.length-1) {
      toUtf(NFR.apply(i).key) match {
        case "Процент ошибок" =>
          for(key <- NFR.apply(i).value.keys){
            key match{
              case "all" => assertList += global.failedRequests.percent.lt(NFR.apply(i).value.apply("all").toInt)
              case _ => assertList+=details(key).failedRequests.percent.lt(NFR.apply(i).value.apply("all").toInt)
            }
          }
        case "99 перцентиль времени выполнения" =>
          for(key <- NFR.apply(i).value.keys){
            key match{
              case "all" => assertList+=global.responseTime.percentile4.lt(NFR.apply(i).value.apply("all").toInt)
              case _ => assertList+=details(key).responseTime.percentile4.lt(NFR.apply(i).value.apply(key).toInt)
            }
          }
        case "95 перцентиль времени выполнения" =>
          for(key <- NFR.apply(i).value.keys){
            key match{
              case "all" => assertList+=global.responseTime.percentile3.lt(NFR.apply(i).value.apply("all").toInt)
              case _ => assertList+=details(key).responseTime.percentile3.lt(NFR.apply(i).value.apply(key).toInt)
            }
          }
        case "75 перцентиль времени выполнения" =>
          for(key <- NFR.apply(i).value.keys){
            key match{
              case "all" => assertList+=global.responseTime.percentile2.lt(NFR.apply(i).value.apply("all").toInt)
              case _ => assertList+=details(key).responseTime.percentile2.lt(NFR.apply(i).value.apply(key).toInt)
            }
          }
        case "50 перцентиль времени выполнения" =>
          for(key <- NFR.apply(i).value.keys){
            key match{
              case "all" => assertList+=global.responseTime.percentile1.lt(NFR.apply(i).value.apply("all").toInt)
              case _ => assertList+=details(key).responseTime.percentile1.lt(NFR.apply(i).value.apply(key).toInt)
            }
          }
        case _ => None
      }
    }
    assertList.toList
  }
}
