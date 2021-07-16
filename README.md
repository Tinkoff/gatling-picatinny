# Gatling Picatinny
![Build](https://github.com/TinkoffCreditSystems/gatling-picatinny/workflows/Build/badge.svg) [![Maven Central](https://img.shields.io/maven-central/v/ru.tinkoff/gatling-picatinny_2.13.svg?color=success)](https://search.maven.org/search?q=ru.tinkoff.gatling-picatinny)

## Table of contents
* [General info](#general-info)
* [Installation](#installation)
* [Usage](#usage)
  * [config](#config)
  * [feeders](#feeders)
  * [influxdb](#influxdb)
  * [profile](#profile)
  * [templates](#templates)
  * [utils](#utils)
  * [assertion](#assertion)
  * [example](#example)
* [Built with](#built-with)
* [Help](#help)
* [License](#license)
* [Authors](#authors)
* [Acknowledgments](#acknowledgments)

## General info
Library with a bunch of usefull functions that extend Gatling DSL and make your performance better.

## Installation

### Using Gatling Template Project
If you are using TinkoffCreditSystems/gatling-template.g8, you already have all dependecies in it. [Gatling Template Project](https://github.com/TinkoffCreditSystems/gatling-template.g8.git)

### Install manualy
Add dependency with version that you need
```scala
libraryDependencies += "ru.tinkoff" %% "gatling-picatinny" % "0.7.0"
```

## Usage

### config
The only class the you need from this module is `SimulationConfig`. It could be used to attach some default variables such as `intensity`, `baseUrl`, `baseAuthUrl` and some others to your scripts. Also it provides functions to get custom variables fom config.

Import:
```scala
import ru.tinkoff.gatling.config.SimulationConfig._
```

Using default variables:

```scala
import ru.tinkoff.gatling.config.SimulationConfig._

val testPlan: Seq[OpenInjectionStep] = List(
    rampUsersPerSec(0).to(intensity).during(rampDuration),
    constantUsersPerSec(intensity).during(stageDuration)
)
```

Using functions to get custom variable:

*simulation.conf*
```
stringVariable: "FOO",
intVariable: 1,
doubleVariable: 3.1415,
duration: {
    durationVariable: 3600s
}
```
```scala
import ru.tinkoff.gatling.config.SimulationConfig._

val stringVariable      = getStringParam("stringVariable")
val intVariable         = getIntParam("intVariable")
val doubleVariable      = getDoubleParam("doubleVariable")
val durationVariable    = getDurationParam("duration.durationVariable")

```

### feeders
This module contains vast number of random feeders. They could be used as regular feeders and realize common needs, i.e random phone number or random digit. Now it supports feeders for dates, numbers and digits, strings, uuids, phones.
There we'll provide some examples, other feeders can be used same way. Now it supports feeders for dates, digits, strings, uuids, phones.

```scala
import ru.tinkoff.gatling.feeders._

//creates feeder with name 'randomString' that gets random string of length 10
val stringFeeder = RandomStringFeeder("randomString", 10)

//creates feeder with name 'digit' that gets random Int digit
val digitFeeder = RandomDigitFeeder("digit")

//creates feeder with name 'uuid' that gets random uuid
val uuidFeeder = RandomUUIDFeeder("uuid")
```
### influxdb 

This module allows you to write custom points to InfluxDB.
 
#### Two kinds of usage
* Write Start/Stop annotations before/after simulation run
* Write custom points to InfluxDB

##### First type denotes start and end of simulation and could be shown in Grafana for example.

Import:

```scala
import ru.tinkoff.gatling.influxdb.Annotations
```

For using you should simply add `with Annotations` for your simulation class:

```scala
class LoadTest extends Simulation with Annotations {
    //some code
}
```

To see annotations in Grafana you need this two queries, where `Perfix` is from `gatling.data.graphite.rootPathPrefix` in `gatling.conf`:

```sql
SELECT "annotation_value"  FROM "${Prefix}" where "annotation" = 'Start'
SELECT "annotation_value"  FROM "${Prefix}" where "annotation" = 'Stop'
```

##### Second type allows you to write various data points from your scenario or test plan

**!DANGER!** Read before use:
* Not intended for load testing of InfluxDB.
* You can easily waste InfluxDB with junk data. Don't use frequently changing keys.
* When recording points in the setUp simulation, a separate script will be created, which will be displayed in the test status in the console and in the final Gatling data.
* Depending on your settings, Gatling will write simulation data to InfluxDB in batches every n seconds. In this case, the timestamp of the custom point will be taken during its recording, which may cause inaccuracies when displaying data.

Import:

```scala
import ru.tinkoff.gatling.influxdb.Annotations._
```

Using:

```scala
//if default prepared Point doesn`t suit you
    Point(configuration.data.graphite.rootPathPrefix, System.currentTimeMillis() * 1000000)
      .addTag(tagName, tagValue)
      .addField(fieldName, fieldValue)

//prepare custom Point*
import io.razem.influxdbclient.Point

  def customPoint(tag: String, value: String) = Point(configuration.data.graphite.rootPathPrefix, System.currentTimeMillis() * 1000000)
    .addTag("myTag", tag)
    .addField("myField", value) //value: Boolean | String | BigDecimal | Long | Double
```

_*_[_Custom Point reference_](https://www.javadoc.io/doc/io.razem/scala-influxdb-client_2.13/0.6.2/io/razem/influxdbclient/Point.html)

```scala
//write custom prepared Point from scenario
      .exec(...)
      .userDataPoint(customPoint("custom_tag", "inside_scenario"))
      .exec(...)

//write default prepared Point from scenario
      .exec(...)
      .userDataPoint("myTag", "tagValue", "myField", "fieldValue")
      //also you can use gatling Expression language for values (could waste DB):
      .userDataPoint("myTag", "${variableFromGatlingSession}", "myField", "${anotherVariableFromSession}")
      .exec(...)

//write Point from Simulation setUp
  setUp(
    firstScenario.inject(atOnceUsers(1))
      //write point after firstScenario execution
      //"write_first_point" the name of the scenario, will be displayed in the simulation stats
      .userDataPoint("write_first_point", customPoint("custom_tag", "between_scenarios"))
      .andThen(
        secondScenario.inject(atOnceUsers(1))
          //similar to simple .userDataPoint, write point after secondScenario execution
          .andThen(
            userDataPoint("write_second_point", "custom_tag", "after_second", "custom_field", "end")
          )
      )
  )
```

### profile
#### Features:
* Load profile configs from HOCON or YAML files
* Common traits to create profiles for any protocol
* HTTP profile as an example

#### Import:

```scala
import ru.tinkoff.gatling.profile._
import pureconfig.generic.auto._
```

#### Using:

HOCON configuration example:
```hocon
{
  name: test-profile
  profile: [
    {
      name: request-a
      url: "http://test.url"
      probability: 50.0
      method: get
    },
    {
      name: request-b
      url: "http://test.url"
      probability: 50.0
      method: post
      body:  "{\"a\": \"1\"}"
    }
  ]
}
```

YAML configuration example:
```yaml
name: test-profile
profile:
  - name: request-a
    url: "http://test.url"
    probability: 50
    method: get
  - name: request-b
    url: "http://test.url"
    probability: 50
    method: post
    body: "{\"a\": \"1\"}"
```

*Simulation setUp*
```scala
  class test extends Simulation {
      val profileConfigName = "profile.conf"
      val someTestPlan      = constantUsersPerSec(intensity) during stageDuration
      val httpProtocol      = http.baseUrl(baseUrl)
      val config: HttpProfileConfig = new ProfileBuilder[HttpProfileConfig].buildFromYaml(profileConfigName)
      val scn: ScenarioBuilder = config.toRandomScenario
      
      setUp(
          scn.inject(
            atOnceUsers(10)
          ).protocols(httpProtocol)
        ).maxDuration(10 )
  }
```

### templates
TBD by v.kalyokin

### utils
#### jwt
#### Features:
* Generates a JWT token using a json template and stores it in a Gatling session, then you can use it to sign requests.

#### Import:

```scala
import ru.tinkoff.gatling.utils.jwt._
```

#### Using:
First you need to prepare jwt generator.
For example
```scala
val jwtGenerator = jwt("HS256", jwtSecretToken)
    .defaultHeader 
    .payloadFromResource("jwtTemplates/payload.json")
```
This will generate tokens with headers:
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```
Payload will be generated from json template, templating is done using [Gatling EL](https://gatling.io/docs/current/session/expression_el/)
```json
{
  "userName": "${randomString}",
  "date": "${simpleDate}",
  "phone": "${randomPhone}"
}
```
Also the JWT generator has a DSL allowing you to:
```scala
jwt("HS256", secret)
.header("""{"alg": "HS256","typ": "JWT", "customField": "customData"}""") //use custom headers from string, it must be valid json
.headerFromResource("jwtTemplates/header.json") //use src/test/resources/jwtTemplates/header.json as header template
.defaultHeader //use default jwt header, algorithm from jwt("HS256", secret), template: {"alg": "$algorithm","typ": "JWT"}
.payload("""{"userName": "${randomString}","date": "${simpleDate}","phone": "${randomPhone}"}""") //use custom payload from string, it must be valid json
.payloadFromResource("jwtTemplates/payload.json") //use src/test/resources/jwtTemplates/payload.json as payload template
```

For sign requests add this to your scenario chain:
```scala
    .exec(_.setJwt(jwtGenerator, "jwtToken")) //generates token and save it to gatling session as "jwtToken"
    .exec(addCookie(Cookie("JWT_TOKEN", "${jwtToken}").withDomain(jwtCookieDomain).withPath("/"))) //set JWT_TOKEN cookie for subsequent requests
```

### assertion

Module helps to load assertion configs from YAML files

#### Import:

```scala
import ru.tinkoff.gatling.assertions.AssertionsBuilder.assertionFromYaml
```

#### Using:
File nfr contains non-functional requirements. 

Requirements supports by Picatinny:

|  requirement|  key |
|---|---|
|  99th percentile of the responseTime | 99 перцентиль времени выполнения  |
|  95th percentile of the responseTime | 95 перцентиль времени выполнения  |
|  75th percentile of the responseTime |  75 перцентиль времени выполнения |
|  50th percentile of the responseTime |  50 перцентиль времени выполнения |
|  percent of the failedRequests |  Процент ошибок |
|  maximum of the responseTime |  Максимальное время выполнения |
 
YAML configuration example:
```yaml
nfr:
  - key: '99 перцентиль времени выполнения'
    value:
      GET /: '500'
      MyGroup / MyRequest: '900'
      request_1: '700' 
      all: '1000'
  - key: 'Процент ошибок'
    value:
      all: '5'
  - key: 'Максимальное время выполнения'
    value:
      GET /: '1000'
      all: '2000'
```

*Simulation setUp*
```scala
  class test extends Simulation {
      
      setUp(
          scn.inject(
            atOnceUsers(10)
          ).protocols(httpProtocol)
        ).maxDuration(10)
      .assertions(assertionFromYaml("src/test/resources/nfr.yml"))
  }
```

### example
See the examples in the examples/ directory.

You can run these from the sbt console with the commands ```project example```
and then ```gatling:testOnly ru.tinkoff.load.example.SampleSimulation```.

Ensure that the correct InfluxDB parameters are specified in gatling.conf and influx.conf.

## Testing
To test your changes use `sbt test`.

## Built with
* Scala version: 2.13.4
* SBT version: 1.4.7
* Gatling version: 3.6.1
* SBT Gatling plugin version: 3.2.1
* SBT CI release plugin version: 1.5.5
* json4s version: 3.6.11
* requests version: 0.2.0
* pureconfig version: 0.14.1
* scalatest version: 3.2.5
* scalacheck version: 1.15.2
* scalamock version: 5.1.0
* generex version: 1.0.2
* jwt-core version: 5.0.0
* scala influxdb client 0.6.3

## Help

telegram: @qa_load

gatling docs: https://gatling.io/docs/current/general

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/TinkoffCreditSystems/gatling-picatinny/tags). 

## Authors

* **Maksim Sitnikov** - *profile module* - [m.sitnikov@tinkoff.ru](m.sitnikov@tinkoff.ru)

* **Chepkasov Sergey** - *influxdb, feeders, config, utils modules* - [s.chepkasov@tinkoff.ru]([s.chepkasov@tinkoff.ru)

* **Kalyokin Vyacheslav** - *templates module* - [v.kalyokin@tinkoff.ru](v.kalyokin@tinkoff.ru)

* **Akhaltsev Ioann** - *founder and spiritual guidance* - [i.akhaltsev@tinkoff.ru](i.akhaltsev@tinkoff.ru)


See also the list of [contributors](https://github.com/TinkoffCreditSystems/gatling-picatinny/graphs/contributors) who participated in this project.

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

TBD
