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

package edu.agh.mindmapd.actors.http

import akka.actor.{Cancellable, ActorRef, Props, Actor}
import edu.agh.mindmapd.json.PollResponse
import edu.agh.mindmapd.actors.{MindMap, MapsSupervisor}
import concurrent.duration._
import edu.agh.mindmapd.model.NodePlusMap
import edu.agh.mindmapd.extensions.Settings

object Poller {

  def props(mapsSupervisor: ActorRef) = Props(classOf[Poller], mapsSupervisor)

  case class Process(since: Long, completer: PollResponse => Unit)

  private case object PollTimeout
  private case object MapsTimeout

}

class Poller(mapsSupervisor: ActorRef) extends Actor {
  import Poller._
  import context.dispatcher

  def receive = initial

  val settings = Settings(context.system)
  var cancellables = List.empty[Cancellable]

  def initial: Receive = {
    case Process(since, completer) =>
      mapsSupervisor ! MapsSupervisor.Subscribe(self, since)
      cancellables ::= context.system.scheduler scheduleOnce (settings.pollTimeout, self, PollTimeout)
      context become waitingForFirst(completer)
  }

  def waitingForFirst(completer: PollResponse => Unit): Receive = {
    case PollTimeout => complete(Nil, completer)

    case MindMap.Changed(node) =>
      cancellables ::= context.system.scheduler scheduleOnce (settings.mapResponseTimeFrame, self, MapsTimeout)
      context become waitingForRest(node :: Nil, completer)
  }

  def waitingForRest(data: List[NodePlusMap], completer: PollResponse => Unit): Receive = {
    case PollTimeout | MapsTimeout => complete(data, completer)

    case MindMap.Changed(node) =>
      context become waitingForRest(node :: data, completer)
  }

  def complete(data: List[NodePlusMap], completer: PollResponse => Unit) {
    cancellables foreach (_.cancel())
    completer(PollResponse(data))
    mapsSupervisor ! MapsSupervisor.Unsubscribe(self)
    context stop self
  }

}
