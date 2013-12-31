/*
 * Copyright 2013 Katarzyna Szawan <kat.szwn@gmail.com>
 *     and Michał Rus <https://michalrus.com/>
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

package edu.agh.mindmapd.model

import java.util.UUID
import spray.json._
import edu.agh.mindmapd.extensions.CustomJsonFormats

object MindNode extends DefaultJsonProtocol with CustomJsonFormats {
  implicit val format = jsonFormat7(apply)
}

case class MindNode(uuid: UUID,
                    map: UUID,
                    parent: Option[UUID],
                    ordering: Double,
                    content: Option[String],
                    hasConflict: Boolean,
                    cloudTime: Long)
