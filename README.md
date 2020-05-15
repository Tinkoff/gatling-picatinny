# Gatling Utils

## Table of contents
* [General info](#general-info)
* [Installation](#installation)
* [Usage](#usage)
  * [config](#config)
  * [feeders](#feeders)
  * [influxdb](#influxdb)
  * [profile](#profile)
  * [templates](#templates)
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
libraryDependencies += "ru.tinkoff" %% "gatling-picatinny" % "0.5.0"
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
This module gives you a possibility to configure simple Scenarios from JSON file. Now it has some fundamental limitations but they will fixed in the future. 

Import:
```scala
import ru.tinkoff.gatling.profile._
```

Using:

*resources/profile/sample_profile.conf*
```
{
   "profile": {
      "name": "awesome_profile",
      "requests": {
         "awesome/url_1/": {
            "method": "GET",
            "url": "awesome/url_1/",
            "prob": 33.33333
         },
         "awesome/url_1/sub_url_2": {
            "method": "GET",
            "url": "awesome/url_1/sub_url_2",
            "prob": 66.66667
         }
      }
   }
}
```
```scala
val profileConfig: Config = ProfileConfigManager.profileConfigLoad(s"profile/sample_profile.conf")

setUp(
    HttpProfile(profileConfig)
      .build()
      .inject(someTestPlan)
      .protocols(someProtocol)
   )
  
```

### templates
TBD by v.kalyokin

### example
See the examples in the examples/ directory.

You can run these from the sbt console with the commands ```project example```
and then ```gatling:testOnly ru.tinkoff.load.example.SampleSimulation```.

Ensure that the correct influxdb parameters are specified in gatling.conf and influx.conf.

## Testing
To test your changes use `sbt test`.

## Built with
* Scala version: 2.12.10
* SBT version: 1.3.4
* Gatling version: 3.3.1
* SBT Gatling plugin version: 1.0.0
* json4s version: 3.6.7
* requests version: 0.2.0
* pureconfig version: 0.12.2
* scalatest version: 3.1.0
* scalacheck version: 1.14.3
* scalamock version: 4.4.0

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


