# Gatling Picatinny

![Build](https://github.com/TinkoffCreditSystems/gatling-picatinny/workflows/Build/badge.svg) [![Maven Central](https://img.shields.io/maven-central/v/ru.tinkoff/gatling-picatinny_2.13.svg?color=success)](https://search.maven.org/search?q=ru.tinkoff.gatling-picatinny) [![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
## Table of contents

* [General info](#general-info)
* [Installation](#installation)
* [Usage](#usage)
    * [config](#config)
    * [feeders](#feeders)
        * [HC Vault feeder](#hc-vault-feeder)
        * [SeparatedValuesFeeder](#separatedvaluesfeeder)
        * [Phone Feeders](#phone-feeders)
    * [influxdb](#influxdb)
    * [profile](#profile)
    * [redis](#redis)
    * [templates](#templates)
    * [utils](#utils)
    * [assertion](#assertion)
    * [transactions](#transactions)
    * [example](#example)
* [Built with](#built-with)
* [Help](#help)
* [License](#license)
* [Authors](#authors)
* [Acknowledgments](#acknowledgments)

## General info

Library with a bunch of useful functions that extend Gatling DSL and make your performance better.

## Installation

### Using Gatling Template Project

If you are using TinkoffCreditSystems/gatling-template.g8, you already have all dependencies in
it. [Gatling Template Project](https://github.com/TinkoffCreditSystems/gatling-template.g8.git)

### Install manually

Add dependency with version that you need

```scala
libraryDependencies += "ru.tinkoff" %% "gatling-picatinny" % "0.11.2"
```

## Usage

### config

The only class that you need from this module is `SimulationConfig`. It could be used to attach some default variables
such as `intensity`, `baseUrl`, `baseAuthUrl` and some others to your scripts. Also, it provides functions to get custom
variables fom config.

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
booleanVariable: true
```

```scala
import ru.tinkoff.gatling.config.SimulationConfig._

val stringVariable = getStringParam("stringVariable")
val intVariable = getIntParam("intVariable")
val doubleVariable = getDoubleParam("doubleVariable")
val durationVariable = getDurationParam("duration.durationVariable")
val booleanVariable = getBooleanParam("booleanVariable")

```

### feeders

This module contains vast number of random feeders. They could be used as regular feeders and realize common needs, i.e.
random phone number or random digit. Now it supports feeders for dates, numbers and digits, strings, uuids, phones.
Basic examples will be provided below. Other feeders can be used in a similar way.

Scala example:

```scala
import ru.tinkoff.gatling.feeders._

//creates feeder with name 'randomString' that gets random string of length 10
val stringFeeder = RandomStringFeeder("randomString", 10)

//creates feeder with name 'digit' that gets random Int digit
val digitFeeder = RandomDigitFeeder("digit")

//creates feeder with name 'uuid' that gets random uuid
val uuidFeeder = RandomUUIDFeeder("uuid")
```

Java example:

```java
import static ru.tinkoff.gatling.javaapi.Feeders.*;

//creates feeder with name 'randomString' that gets random string of length 10
Iterator<Map<String, Object>> stringFeeder = RandomStringFeeder("randomString", 10);

//creates feeder with name 'digit' that gets random Int digit
Iterator<Map<String, Object>> digitFeeder = RandomDigitFeeder("digit");

//creates feeder with name 'uuid' that gets random uuid
Iterator<Map<String, Object>> uuidFeeder = RandomUUIDFeeder("uuid");

```

Kotlin example:

```kotlin
import ru.tinkoff.gatling.javaapi.Feeders.*

//creates feeder with name 'randomString' that gets random string of length 10
val stringFeeder = RandomStringFeeder("string", 10)

//creates feeder with name 'digit' that gets random Int digit
val digitFeeder = RandomDigitFeeder("digit")

//creates feeder with name 'uuid' that gets random uuid
val uuidFeeder = RandomUUIDFeeder("uuid")
```

#### HC Vault feeder

Creates feeder capable of retrieving secret data from HC Vault

- authorises via approle;
- uses v1 API;
- works with kv Secret Engine;
- does not iterate over keys, returns full map with keys it found on each call;
- params:
    - vaultUrl - vault URL *e.g. "https://vault.ru"*
    - secretPath - path to secret data within your vault *e.g. "testing/data"*
    - roleId - approle login
    - secretId - approle password
    - keys - list of keys you are willing to retrieve from vault

Scala example:
```scala
  val vaultFeeder = VaultFeeder(vaultUrl, secretPath, roleId, secretId, keys)
```

Java example:
```Java
  Iterator<Map<String, Object>> vaultFeeder = VaultFeeder(vaultUrl, secretPath, roleId, secretId, keys);
```

Kotlin example:
```Kotlin
  val vaultFeeder = VaultFeeder(vaultUrl, secretPath, roleId, secretId, keys)
```

#### SeparatedValuesFeeder

Creates a feeder with separated values from a source String, Seq[String] or Seq[Map[String, Any]].

- params:
    - paramName - feeder name
    - source - data source
    - separator - ",", ";", "\t" or other delimiter which separates values. You can also use following methods for the
      most common separators: .csv(...), .ssv(...), .tsv(...)

Get separated values from a source: String

Scala example:
```scala
val sourceString = "v21;v22;v23"
val separatedValuesFeeder: FeederBuilderBase[String] =
  SeparatedValuesFeeder("someValues", sourceString, ';') // Vector(Map(someValues -> v21), Map(someValues -> v22), Map(someValues -> v23))
```

Java example:
```Java
  Iterator<Map<String, Object>> vaultFeeder = VaultFeeder(vaultUrl, secretPath, roleId, secretId, keys);
```

Kotlin example:
```Kotlin
  val vaultFeeder = VaultFeeder(vaultUrl, secretPath, roleId, secretId, keys)
```

Get separated values from a source: Seq[String]

Scala example:
```scala
val sourceSeq = Seq("1,two", "3,4")
val separatedValuesFeeder: FeederBuilderBase[String] =
  SeparatedValuesFeeder.csv("someValues", sourceSeq) // Vector(Map(someValues -> 1), Map(someValues -> two), Map(someValues -> 3), Map(someValues -> 4))
```

Java example:
```Java
List<Map<String, Object>> sourceList = Arrays.asList("1,two", "3,4");
Iterator<Map<String, Object>> separatedValuesFeeder = SeparatedValuesFeeder.csv("someValues", sourceList);
```

Kotlin example:
```Kotlin
var sourceList = listOf("1,two", "3,4")
var separatedValuesFeeder1 = SeparatedValuesFeeder.csv("someValues", sourceList)
```

Get separated values from a source: Seq[Map[String, Any]]

Scala example:
```scala
val vaultFeeder: FeederBuilderBase[String] = Vector(
  Map(
    "HOSTS" -> "host11,host12",
    "USERS" -> "user11",
  ),
  Map(
    "HOSTS" -> "host21,host22",
    "USERS" -> "user21,user22,user23",
  ),
)
val separatedValuesFeeder: FeederBuilderBase[String] =
  SeparatedValuesFeeder(None, vaultFeeder.readRecords, ',') // Vector(Map(HOSTS -> host11), Map(HOSTS -> host12), Map(USERS -> user11), Map(HOSTS -> host21), Map(HOSTS -> host22), Map(USERS -> user21), Map(USERS -> user22), Map(USERS -> user23))
```

Java example:
```Java
List<Map<String, Object>> vaultData = Arrays.asList(Map.of("HOSTS","host11,host12"), Map.of("USERS", "user21,user22,user23"));
Iterator<Map<String, Object>> separatedValuesFeeder = SeparatedValuesFeeder.apply(Optional.empty(), vaultData, ',');
```

Kotlin example:
```Kotlin
var sourceList = listOf(Map.of("HOSTS", "host11,host12"), Map.of("USERS", "user21,user22,user23"))
var separatedValuesFeeder1 = SeparatedValuesFeeder.csv(null, sourceList)
```

#### Phone Feeders

Creates a feeder with phone numbers with formats from json file or `case class PhoneFormat`

Simple phone feeder

Scala example:
```scala
val simplePhoneNumber: Feeder[String] = RandomPhoneFeeder("simplePhoneFeeder")
```

Java example:
```Java
Iterator<Map<String, Object>> simplePhoneNumber = RandomPhoneFeeder("simplePhoneFeeder");
```

Kotlin example:
```Kotlin
val simplePhoneNumber = RandomPhoneFeeder("simplePhoneNumber")
```

Phone feeder with custom formats

Scala example:
```scala
 val ruMobileFormat: PhoneFormat = PhoneFormat(
  countryCode = "+7",
  length = 10,
  areaCodes = Seq("903", "906", "908"),
  prefixes = Seq("55", "81", "111"),
  format = "+X XXX XXX-XX-XX")

  val randomPhoneNumber: Feeder[String]                 =
  RandomPhoneFeeder("randomPhoneNumber", ruMobileFormat)
```

Java example:
```Java
PhoneFormat ruMobileFormat = PhoneFormatBuilder.apply("+7", 10, Arrays.asList("945", "946"), "+X XXX XXX-XX-XX", Arrays.asList("55", "81", "111"));
Iterator<Map<String, Object>> randomPhoneNumber = RandomPhoneFeeder("randomPhoneNumber", ruMobileFormat);
```

Kotlin example:
```Kotlin
val ruMobileFormat = PhoneFormatBuilder.apply(
        "+7",
        10,
        listOf("945", "946"),
        "+X XXX XXX-XX-XX",
        listOf("55", "81", "111")
    )
val randomPhoneNumber = RandomPhoneFeeder("randomPhoneNumber", ruMobileFormat)
```

Phone feeder with custom formats with file

Creates file with formats, for example RESOURCES/phoneTemplates/ru.json

```json
{
  "formats": [
    {
      "countryCode": "+7",
      "length": 10,
      "areaCodes": ["903", "906"],
      "prefixes": ["123", "321", "132", "231"],
      "format": "+X(XXX)XXXXXXX"
    },
    {
      "countryCode": "8",
      "length": 10,
      "areaCodes": ["495", "499"],
      "prefixes": ["81", "82", "83"],
      "format": "X(XXX)XXX-XX-XX"
    }
  ]
}
```

Scala example:
```scala
val phoneFormatsFromFile: String   = "phoneTemplates/ru.json"
val randomE164PhoneNumberFromJson: Feeder[String]     =
    RandomPhoneFeeder("randomE164PhoneNumberFile", phoneFormatsFromFile, TypePhone.E164PhoneNumber)
```

Java example:
```Java
String phoneFormatsFromFile = "phoneTemplates/ru.json";
Iterator<Map<String, Object>> randomE164PhoneNumberFromJson = RandomPhoneFeeder("randomE164PhoneNumberFile", phoneFormatsFromFile, TypePhone.E164PhoneNumber());
```

Kotlin example:
```Kotlin
val phoneFormatsFromFile = "phoneTemplates/ru.json"
val randomE164PhoneNumberFromJson = RandomPhoneFeeder("randomE164PhoneNumberFile", phoneFormatsFromFile, TypePhone.E164PhoneNumber())
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

To see annotations in Grafana you need this two queries, where `Perfix` is from `gatling.data.graphite.rootPathPrefix`
in `gatling.conf`:

```sql
SELECT "annotation_value"  FROM "${Prefix}" where "annotation" = 'Start'
SELECT "annotation_value"  FROM "${Prefix}" where "annotation" = 'Stop'
```

##### Second type allows you to write various data points from your scenario or test plan

**!DANGER!** Read before use:

* Not intended for load testing of InfluxDB.
* You can easily waste InfluxDB with junk data. Don't use frequently changing keys.
* When recording points in the setUp simulation, a separate script will be created, which will be displayed in the test
  status in the console and in the final Gatling data.
* Depending on your settings, Gatling will write simulation data to InfluxDB in batches every n seconds. In this case,
  the timestamp of the custom point will be taken during its recording, which may cause inaccuracies when displaying
  data.

Import:

```scala
import ru.tinkoff.gatling.influxdb.Annotations._
```

Using:

```scala
//if default prepared Point doesn't suit you
Point(configuration.data.graphite.rootPathPrefix, System.currentTimeMillis() * 1000000)
  .addTag(tagName, tagValue)
  .addField(fieldName, fieldValue)

//prepare custom Point*

import io.razem.influxdbclient.Point

def customPoint(tag: String, value: String) = Point(configuration.data.graphite.rootPathPrefix, System.currentTimeMillis() * 1000000)
  .addTag("myTag", tag)
  .addField("myField", value) //value: Boolean | String | BigDecimal | Long | Double
```

_*_[_Custom Point
reference_](https://www.javadoc.io/doc/io.razem/scala-influxdb-client_2.13/0.6.2/io/razem/influxdbclient/Point.html)

```scala
//write custom prepared Point from scenario
.exec(
...)
.userDataPoint(customPoint("custom_tag", "inside_scenario"))
  .exec(
...)

//write default prepared Point from scenario
.exec(
...)
.userDataPoint("myTag", "tagValue", "myField", "fieldValue")
  //also you can use gatling Expression language for values (could waste DB):
  .userDataPoint("myTag", "${variableFromGatlingSession}", "myField", "${anotherVariableFromSession}")
  .exec(
...)

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
      body: "{\"a\": \"1\"}"
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
  val someTestPlan = constantUsersPerSec(intensity) during stageDuration
  val httpProtocol = http.baseUrl(baseUrl)
  val config: HttpProfileConfig = new ProfileBuilder[HttpProfileConfig].buildFromYaml(profileConfigName)
  val scn: ScenarioBuilder = config.toRandomScenario

  setUp(
    scn.inject(
      atOnceUsers(10)
    ).protocols(httpProtocol)
  ).maxDuration(10)
}
```

#### New style profile:

New profile YAML configuration example:

```yaml
apiVersion: link.ru/v1alpha1
kind: PerformanceTestProfiles
metadata:
  name: performance-test-profile
  description: performance test profile
spec:
  profiles:
    - name: maxPerf
      period: 10.05.2022 - 20.05.2022
      protocol: http
      profile:
        - request: request-1
          intensity: 100 rph
          groups: ["Group1"]
          params:
            method: POST
            path: /test/a
            headers:
              - 'Content-Type: application/json'
              - 'Connection: keep-alive'
            body: '{"a": "b"}'
        - request: request-2
          intensity: 200 rph
          groups: ["Group1", "Group2"]
          params:
            method: GET
            path: /test/b
            body: '{"c": "d"}'
        - request: request-3
          intensity: 200 rph
          groups: [ "Group1", "Group2" ]
          params:
            method: GET
            path: /test/c
            body: '{"e": "f"}'
```

Optional fields: groups, headers, body.

If there are no required fields, an exception will be thrown for the missing field.

*Simulation setUp*

Scala example:
```scala
class Debug extends Simulation {
  val pathToProfile = "path/to/profile.yml"
  val scn = ProfileBuilderNew.buildFromYaml(pathToProfile).selectProfile("maxPerf").toRandomScenario

  setUp(
    scn.inject(
      atOnceUsers(10)
    ).protocols(httpProtocol)
  )
          .maxDuration(10)
}
```

Java example:

```java
import ru.tinkoff.gatling.javaapi.profile.ProfileBuilderNew;

public class Debug extends Simulation {

  public static ScenarioBuilder scn = ProfileBuilderNew
          .buildFromYaml("path/to/profile.yml")
          .selectProfile("maxPerf")
          .toRandomScenario();

  {
    setUp(
            scn.injectOpen(atOnceUsers(1))
    ).protocols(httpProtocol);
  }
}
```

Kotlin example:
```kotlin
import ru.tinkoff.gatling.javaapi.profile.ProfileBuilderNew

class Debug : Simulation() {
  val scn: ScenarioBuilder = ProfileBuilderNew
    .buildFromYaml("path/to/profile.yml")
    .selectProfile("maxPerf")
    .toRandomScenario()

  init {
    setUp(
      scn.injectOpen(atOnceUsers(1)),
    ).protocols(httpProtocol)
  }
}
```

### redis

This module allows you to use Redis commands.

#### Features:

- Support Redis commands: SADD, DEL, SREM
- Support Gatling EL

#### Read before use:

- Мethods are not taken into account in statistics Gatling.
- Not intended for load testing of Redis.

#### Import:

```scala
import com.redis.RedisClientPool
import ru.tinkoff.gatling.redis.RedisActionBuilder._
```

#### Using:

First you need to prepare RedisClientPool:

```scala
val redisPool = new RedisClientPool(redisUrl, 6379)
```

Add the Redis commands to your scenario chain:

```scala
.exec(redisPool.SADD("key", "values", "values")) //add the specified members to the set stored at key
  .exec(redisPool.DEL("key", "keys")) //removes the specified keys
  .exec(redisPool.SREM("key", "values", "values")) //remove the specified members from the set stored at key
```

### templates

This module contains some syntax extensions for http requests with json body. It allows embed json-body in request
with `jsonBody` method for `HttpRequestBuilder`. And this module is provided ability to send request body templates from
files in resource subfolder `resources/templates` by filename. Sending of templates may be done with
method `postTemplate` from trait `Templates`

#### jsonBody

This part contains http request Json body DSL.

For use this you need import this:

```scala
import ru.tinkoff.gatling.templates.HttpBodyExt._
import ru.tinkoff.gatling.templates.Syntax._
```

Then use described later constructions for embed jsonBody in http requests. For example, you write something like this:

```scala
class SampleScenario {
  val sendJson: ScenarioBuilder =
    scenario("Post some")
      .exec(
        http("PostData")
          .post(url)
          .jsonBody(
            "id" - 23, // in json - "id" : 23 
            "name", // in json it interpreted as - "name" : get value from session variable ${name}
            "project" - ( // in json - "project" : { ... }
              "id" ~ "projectId", // in json - "id" : value from session var ${projectId}
              "name" - "Super Project", // in json - "name": "Super Project"
              "sub" > (1, 2, 3, 4, 5, 6) // in json - "sub" : [ 1,2,3,4,5,6 ]
            )
          )
      )
}
```

As result this scenario sent POST request with body:

```json
{
  "id": 23,
  "name": "Test",
  "project": {
    "id": 23421,
    "name": "Super Project",
    "sub": [
      1,
      2,
      3,
      4,
      5,
      6
    ]
  }
}
```

As you can see in the example:

- construction `"some_name" - <val>` map to `"some_name": <val>` in json;
- construction `"varName"` map to `"varName" : <get value from session variable ${varName}>` in json;
- construction `"some_name" ~ "sesVar"` map to `"some_name" : <value from session var ${sesVar}>` in json;
- `"some_name" > (<...items>)` map to array field `"some_name": [ ...items ]` in json;
- `"some_name" - (<...fields>)` map to object field `"some_name": { ...fields }` in json;

#### postTemplate

Suppose in folder resources/templates contains this:

```shell
$ tree resources/
.
├── gatling.conf
├── logback.xml
├── pools
│   └── example_pool.csv
├── simulation.conf
└── templates
    └── example_template1.json
    └── example_template2.json
```

For use templates in `resources/templates` you need import trait `Templates`.

```scala
import ru.tinkoff.gatling.templates.Templates._
```

Then add this trait to your Scenario and use `postTemplate` method like show later:

```scala
class SampleScenario extends Templates {
  val sendTemplates: ScenarioBuilder =
    scenario("Templates scenario")
      .exec(postTemplate("example_template1", "/post_route"))
      .exec(postTemplate("example_template2", "/post_route"))
}
```

This Scenario will send 2 post requests one with body from `example_template1.json`, second with body
from `example_template2.json` to route `$baseUrl/post_route`. In template files you may use
[gatling expression syntax](https://gatling.io/docs/gatling/reference/current/session/expression_el/).

### utils

#### jwt

#### Features:

* Generates a JWT token using a json template and stores it in a Gatling session, then you can use it to sign requests.

#### Import:

Scala:
```scala
import ru.tinkoff.gatling.utils.jwt._
```

Java:
```java
import ru.tinkoff.gatling.utils.jwt.*;
import static ru.tinkoff.gatling.javaapi.utils.Jwt.*;
```

Kotlin
```kotlin
import ru.tinkoff.gatling.javaapi.utils.Jwt.*
```

#### Using:

First you need to prepare jwt generator. For example

Scala
```scala
val jwtGenerator = jwt("HS256", jwtSecretToken)
  .defaultHeader
  .payloadFromResource("jwtTemplates/payload.json")
```

Java:
```java
static JwtGeneratorBuilder jwtGenerator = jwt("HS256", "jwtSecretToken")  
        .defaultHeader
        .payloadFromResource("jwtTemplates/payload.json");
```

Kotlin
```kotlin
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

Payload will be generated from json template, templating is done
using [Gatling EL](https://gatling.io/docs/current/session/expression_el/)

```json
{
  "userName": "${randomString}",
  "date": "${simpleDate}",
  "phone": "${randomPhone}"
}
```

Also, the JWT generator has a DSL allowing you to (for java and kotlin similarly):

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

Java:
```java
.exec(setJwt(jwtGenerator, "jwtToken"))  //generates token and save it to gatling session as "jwtToken"
    .exec(addCookie(Cookie("JWT_TOKEN", "#{jwtToken}").withDomain("jwtCookieDomain").withPath("/"))) //set JWT_TOKEN cookie for subsequent requests
```

Kotlin
```kotlin
.exec(setJwt(jwtGenerator, "jwtToken"))  //generates token and save it to gatling session as "jwtToken"
  .exec(addCookie(Cookie("JWT_TOKEN", "#{jwtToken}").withDomain("jwtCookieDomain").withPath("/"))) //set JWT_TOKEN cookie for subsequent requests
```

### assertion

Module helps to load assertion configs from YAML files

#### Import:

```scala
import ru.tinkoff.gatling.assertions.AssertionsBuilder.assertionFromYaml
```

#### Using:

File nfr contains non-functional requirements.

Requirements supported by Picatinny:

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

*Scala example*

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

*Java example*

```java
  import static ru.tinkoff.gatling.javaapi.Assertions.assertionFromYaml;

  class test extends Simulation {

  setUp(
    scn.inject(
      atOnceUsers(10)
    ).protocols(httpProtocol)
  ).maxDuration(10)
    .assertions(assertionFromYaml("src/test/resources/nfr.yml"))
```

*Kotlin example*

```kotlin
  import ru.tinkoff.gatling.javaapi.Assertions.assertionFromYaml;

  class test extends Simulation {

  setUp(
    scn.inject(
      atOnceUsers(10)
    ).protocols(httpProtocol)
  ).maxDuration(10)
    .assertions(assertionFromYaml("src/test/resources/nfr.yml"))
```

### transactions

This extension introduce new syntax (`startTransaction`/`endTransaction`) for gatling scenario. Transaction is union of
actions, that able to measure summary response time of actions with pauses. It is same as groups, but response time
measuring include pauses, and you may pass endTime manually. That make possible write something like:

```scala
exec(Actions.createEntity())
  .startTransaction("transaction1")
  .doWhile(_ ("i").as[Int] < 10)(
    feed(feeder)
      .exec(Actions.insertTest())
      .pause(2)
      .exec(Actions.selectTest)
  )
  .endTransaction("transaction1")
  .exec(Actions.batchTest)
  .exec(Actions.selectAfterBatch)

```

Java example:

```java
exec(Actions.createEntity())
  .exec(startTransaction("transaction1"))
  .exec(Actions.insertTest())
  .pause(2)
  .exec(Actions.selectTest)
  .exec(endTransaction("transaction1"))
  .exec(Actions.batchTest)
  .exec(Actions.selectAfterBatch)
```

Kotlin example:

```kotlin
exec(Actions.createEntity())
  .exec(startTransaction("transaction1"))
  .exec(Actions.insertTest())
  .pause(2)
  .exec(Actions.selectTest)
  .exec(endTransaction("transaction1"))
  .exec(Actions.batchTest)
  .exec(Actions.selectAfterBatch)
```

#### Usage:

For use this you need gatling with version greater or equal than **3.6.1** and import this in Scenario and Simulations:

```scala
import ru.tinkoff.gatling.transactions.Predef._
```

**Attention!**
*Your simulation should inherit the class `SimulationWithTransactions` instead of `Simulation`, then the transaction
mechanism will work correctly.*

#### Example Simulation:

```scala
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
..

.
import ru.tinkoff.gatling.transactions.Predef._

object DebugScenario {
  val scn: ScenarioBuilder = scenario("Debug")
    .exec(Actions.createEntity())
    .startTransaction("transaction1")
    .doWhile(_ ("i").as[Int] < 10)(
      feed(feeder)
        .exec(Actions.insertTest())
        .pause(2)
        .exec(Actions.selectTest)
    )
    .endTransaction("transaction1")
    .exec(Actions.batchTest)
    .exec(Actions.selectAfterBatch)
}

class DebugTest extends SimulationWithTransactions {

  setUp(
    DebugScenario.scn.inject(atOnceUsers(1))
  ).protocols(dataBase)

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

* Scala version: 2.13.8
* SBT version: 1.6.1
* Gatling version: 3.7.4
* SBT Gatling plugin version: 4.1.2
* SBT CI release plugin version: 1.5.10
* json4s version: 4.0.2
* pureconfig version: 0.17.1
* scalatest version: 3.2.10
* scalacheck version: 1.15.4
* scalamock version: 5.2.0
* generex version: 1.0.2
* jwt-core version: 5.0.0
* scala influxdb client 0.6.3

## Help

telegram: @qa_load

gatling docs: https://gatling.io/docs/current/general

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see
the [tags on this repository](https://github.com/TinkoffCreditSystems/gatling-picatinny/tags).

## Authors

* **Maksim Sitnikov** - *profile module* - [m.sitnikov@tinkoff.ru](m.sitnikov@tinkoff.ru)

* **Chepkasov Sergey** - *influxdb, feeders, config, utils modules* - [s.chepkasov@tinkoff.ru]([s.chepkasov@tinkoff.ru)

* **Kalyokin Vyacheslav** - *templates module* - [v.kalyokin@tinkoff.ru](v.kalyokin@tinkoff.ru)

* **Akhaltsev Ioann** - *founder and spiritual guidance* - [i.akhaltsev@tinkoff.ru](i.akhaltsev@tinkoff.ru)

See also the list of [contributors](https://github.com/TinkoffCreditSystems/gatling-picatinny/graphs/contributors) who
participated in this project.

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

TBD
