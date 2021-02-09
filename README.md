# Gatling Picatinny
[![Build Status](https://travis-ci.com/TinkoffCreditSystems/gatling-picatinny.svg?branch=master)](https://travis-ci.com/TinkoffCreditSystems/gatling-picatinny) [![Maven Central](https://img.shields.io/maven-central/v/ru.tinkoff/gatling-picatinny_2.12.svg?color=success)](https://search.maven.org/search?q=ru.tinkoff.gatling-picatinny)

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
libraryDependencies += "ru.tinkoff" %% "gatling-picatinny" % "0.6.0"
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
This module includes Annotation manager. It can write annotations to InfluxDB that denote start and end of simulation and could be shown in Grafana for example.

Import:
```scala
import ru.tinkoff.gatling.influxdb.Annotations
```

For  using you should simply add `with Annotations` for your simulation class:
```scala
class LoadTest extends Simulation with Annotations{
    //some code
}
```

To see annotations in grafana you need this two queries, where `Perfix` is from `gatling.data.graphite.rootPathPrefix` in `gatling.conf`:
```sql
SELECT "annotation_value"  FROM "${Prefix}" where "annotation" = 'Start'
SELECT "annotation_value"  FROM "${Prefix}" where "annotation" = 'Stop'
```
### profile
#### Features:
* Load profile configs from HOCON or YAML files
* Common traits to create profiles for any protocol
* HTTP profile as an example

#### Import:

```scala
import ru.tinkoff.gatling.profile._
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

### example
See the examples in the examples/ directory.

You can run these from the sbt console with the commands ```project example```
and then ```gatling:testOnly ru.tinkoff.load.example.SampleSimulation```.

Ensure that the correct InfluxDB parameters are specified in gatling.conf and influx.conf.

## Testing
To test your changes use `sbt test`.

## Built with
* Scala version: 2.13.4
* SBT version: 1.4.4
* Gatling version: 3.5.0
* SBT Gatling plugin version: 3.2.1
* SBT git plugin version: 1.0.0
* SBT git-versioning plugin version: 1.4.0
* SBT sonatype plugin version: 3.9.2
* SBT gpg plugin version: 0.2.1
* json4s version: 3.6.10
* requests version: 0.2.0
* pureconfig version: 0.14.0
* scalatest version: 3.2.0
* scalacheck version: 1.14.3
* scalamock version: 5.0.0
* generex version: 1.0.2
* jwt-core version: 4.2.0

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
