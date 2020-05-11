/*
 * Copyright 2020 Frédéric Cabestre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sigusr.mqtt.impl.protocol

import java.net.InetSocketAddress

import cats.effect.implicits._
import cats.effect.{ Blocker, Concurrent, ContextShift, Sync }
import cats.implicits._
import enumeratum.values._
import fs2.concurrent.Queue
import fs2.io.tcp.SocketGroup
import fs2.{ Pipe, Stream }
import net.sigusr.mqtt.impl.frames.Frame
import net.sigusr.mqtt.impl.protocol.Transport.Direction.{ In, Out }
import scodec.Codec
import scodec.stream.{ StreamDecoder, StreamEncoder }

import scala.concurrent.duration.FiniteDuration

sealed case class TransportConfig(
  host: String,
  port: Int,
  readTimeout: Option[FiniteDuration] = None,
  writeTimeout: Option[FiniteDuration] = None,
  numReadBytes: Int = 4096,
  traceMessages: Boolean = false)

trait Transport[F[_]] {

  def inFrameStream: Stream[F, Frame]

  def outFrameStream: Pipe[F, Frame, Unit]

}

object Transport {

  sealed abstract class Direction(val value: Char, val color: String, val active: Boolean) extends CharEnumEntry
  object Direction extends CharEnum[Direction] {
    case class In(override val active: Boolean) extends Direction('←', Console.YELLOW, active)
    case class Out(override val active: Boolean) extends Direction('→', Console.GREEN, active)

    val values: IndexedSeq[Direction] = findValues
  }

  private def tracingPipe[F[_]: Concurrent: ContextShift](d: Direction): Pipe[F, Frame, Frame] = frames => for {
    frame <- frames
    _ <- Stream.eval(Sync[F]
      .delay(println(s" ${d.value} ${d.color}$frame${Console.RESET}"))
      .whenA(d.active))
  } yield frame

  private def connect[F[_]: Concurrent: ContextShift](
    transportConfig: TransportConfig,
    in: Queue[F, Frame],
    out: Queue[F, Frame]): F[Unit] = {
    Blocker[F].use { blocker =>
      SocketGroup[F](blocker).use { socketGroup =>
        socketGroup.client[F](new InetSocketAddress(transportConfig.host, transportConfig.port)).use { socket =>
          val outFiber = out.dequeue
            .through(tracingPipe(Out(transportConfig.traceMessages)))
            .through(StreamEncoder.many[Frame](Codec[Frame].asEncoder).toPipeByte)
            .through(socket.writes(transportConfig.writeTimeout)).compile.drain

          val inFiber = socket.reads(transportConfig.numReadBytes, transportConfig.readTimeout)
            .through(StreamDecoder.many[Frame](Codec[Frame].asDecoder).toPipeByte)
            .through(tracingPipe(In(transportConfig.traceMessages)))
            .through(in.enqueue).onComplete {
              Stream.eval(Concurrent[F].delay(println("CCCCCCCCCCCCCCCCCCC")))
            }.compile.drain

          Concurrent[F].race(inFiber, outFiber).map(_ => ())
        }
      }
    }
  }

  def apply[F[_]: Concurrent: ContextShift](
    transportConfig: TransportConfig): F[Transport[F]] = for {
    in <- Queue.bounded[F, Frame](QUEUE_SIZE)
    out <- Queue.bounded[F, Frame](QUEUE_SIZE)
    _ <- connect(transportConfig, in, out).start
  } yield new Transport[F] {

    def outFrameStream: Pipe[F, Frame, Unit] = out.enqueue

    def inFrameStream: Stream[F, Frame] = in.dequeue

  }
}
