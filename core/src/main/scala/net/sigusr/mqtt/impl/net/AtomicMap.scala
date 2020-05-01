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

package net.sigusr.mqtt.impl.net

import cats.effect.Concurrent
import cats.effect.concurrent.Ref
import cats.implicits._

trait AtomicMap[F[_], K, V] {

  def update(key: K, result: V): F[Unit]

  def remove(key: K): F[Option[V]]

}

object AtomicMap {

  def apply[F[_]: Concurrent, K, V]: F[AtomicMap[F, K, V]] = for {

    mm <- Ref.of[F, Map[K, V]](Map.empty)

  } yield new AtomicMap[F, K, V]() {

    override def update(key: K, result: V): F[Unit] = mm.update(m => m.updated(key, result))

    override def remove(key: K): F[Option[V]] = mm.modify(m => (m.removed(key), m.get(key)))
  }
}
