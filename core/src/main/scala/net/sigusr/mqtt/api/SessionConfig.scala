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

package net.sigusr.mqtt.api

import net.sigusr.mqtt.impl.protocol.DEFAULT_KEEP_ALIVE

sealed case class Will(retain: Boolean, qos: QualityOfService, topic: String, message: String)

sealed case class SessionConfig(
    clientId: String,
    keepAlive: Int = DEFAULT_KEEP_ALIVE,
    cleanSession: Boolean = true,
    will: Option[Will] = None,
    user: Option[String] = None,
    password: Option[String] = None
)
