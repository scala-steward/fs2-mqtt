[akka-mqtt]: https://github.com/fcabestre/Scala-MQTT-client
[akka]: http://akka.io
[apache-licence]: http://github.com/user-signal/fs2-mqtt/blob/master/LICENSE
[build-status-icon]: https://travis-ci.org/user-signal/fs2-mqtt.svg?branch=master
[build-status]: https://travis-ci.org/user-signal/fs2-mqtt
[cats-effect-IO]: https://typelevel.org/cats-effect/datatypes/io.html
[cats-effect-retry]: https://cb372.github.io/cats-retry
[cats-effet]: https://github.com/typelevel/cats-effect
[cats]: https://typelevel.org/cats
[ci]: https://travis-ci.org/user-signal/fs2-mqtt
[coverage-status-icon]: https://coveralls.io/repos/user-signal/fs2-mqtt/badge.png?branch=master
[coverage-status]: https://coveralls.io/r/user-signal/fs2-mqtt?branch=master
[fs2]: https://fs2.io
[local publisher]: https://github.com/user-signal/fs2-mqtt/blob/master/examples/src/main/scala/net/sigusr/mqtt/examples/LocalPublisher.scala
[local subscriber]: https://github.com/user-signal/fs2-mqtt/blob/master/examples/src/main/scala/net/sigusr/mqtt/examples/LocalSubscriber.scala
[monix]: https://monix.io
[monocle]: https://optics-dev.github.io/Monocle
[mosquitto]: http://mosquitto.org
[practical-fp]: https://leanpub.com/pfp-scala
[scala-js]: https://www.scala-js.org
[scala]: https://www.scala-lang.org
[scodec-stream]: https://github.com/scodec/scodec-stream
[scodec]: http://scodec.org
[skunk-repo]: https://github.com/tpolecat/skunk
[skunk-talk]: https://youtu.be/NJrgj1vQeAI
[sonatype]: https://oss.sonatype.org/index.html#nexus-search;quick~fs2-mqtt
[tpolecat]: https://twitter.com/tpolecat
[zio]: https://zio.dev

# A pure functional Scala MQTT client library [![Build Status][build-status-icon]][build-status] [![codecov](https://codecov.io/gh/user-signal/fs2-mqtt/branch/master/graph/badge.svg?token=08C9HM2J0L)](https://codecov.io/gh/user-signal/fs2-mqtt)
## Back then...

Late 2014, I initiated an [MQTT client library for Scala][akka-mqtt] side project. 
My purpose was to learn me some [Akka][akka] while trying to deliver something potentially useful. I quickly 
found the excellent [scodec][scodec] library to encode/decode *MQTT* protocol frames, making
this part of the work considerably easier, with a concise and very readable outcome.

More recently, while getting more and more interest in pure functional programming in *Scala*, in had the chance to see
this amazing talk on [Skunk][skunk-talk] from [@tpolecat][tpolecat]. It's about 
building, from the ground up, a data access library for *Postgres* based on [FS2][fs2] and… *scodec*.

## Oops!… I did it again.

I rushed to [Skunk][skunk-repo], which as been inspirational, and took the opportunity of these 
lock down days to learn a lot about [cats][cats], [cats effects][cats-effet] 
and of course *FS2*. I even found the integration between *FS2* and *scodec*, [scodec-stream][scodec-stream], 
to be utterly useful.

With all these tools at hand, and the book [Practical-FP in Scala: A hands-on approach][practical-fp]
on my desk, it has been quite (sic) easy to connect everything together and build this purely functional Scala MQTT
client library.

## Current status

This library is build in the *tagless final* style in order to make it, as much as possible, *IO monad* agnostic for the
client code using it. It's internals are nevertheless mainly build around *FS2*, *cats effetcs* typeclasses and concurrency 
primitives.  

It implements almost all the *MQTT* `3.1.1` protocol, managing (re)connections and in flight messages, and allows interacting 
with a [Mosquitto][mosquitto] broker. It does not support *MQTT* `5` and, to tell the truth, this is not even envisioned! 
Still, there's work ahead:
 * finer grained configuration (e. g. number of in flight messages)
 * a proper documentation
 * shame on me, far more test coverage /o\
 * performance evaluation
 * trying to consider *Scala.js*,
 * …

For examples on how to use it, you can have a look at the [local subscriber][local subscriber] or the [local publisher][local publisher] 
code. The former is build using [ZIO][zio] while the later is based on [Monix][monix].

## Releases

Artifacts are available at [Sonatype OSS Repository Hosting service][sonatype], even the ```SNAPSHOTS``` automatically
built by [Travis CI][ci]. To include the Sonatype repositories in your SBT build you should add,

```scala
resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
)
```

In case you want to easily give a try to this library, without the burden of adding resolvers, there is a release synced
to Maven Central. In this case just add,

```scala
scalaVersion := "3.0.0"

libraryDependencies ++= Seq(
    "net.sigusr" %% "fs2-mqtt" % "0.5.0"
)
```

## Dependencies

Roughly speaking this library depends on:
 * [Scala][scala] of course, but not [Scala.js][scala-js] even thought this should be fairly easy…
 * [FS2][fs2] 
 * [scodec][scodec] and [scodec-stream][scodec-stream]
 * [Cats effect][cats-effet] for some internal concurrency stuff
 * [Cats effects retry][cats-effect-retry] to manage connection attempts
 
This library should work seamlessly with various compatible IO monads: [cats effects IO][cats-effect-IO] 
of course, but [Monix][monix] and [ZIO][zio] as well as both support *cats effects* typeclasses.

## License

This work is licenced under an [Apache Version 2.0 license][apache-licence]
