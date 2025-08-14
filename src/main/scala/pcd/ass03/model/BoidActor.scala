package pcd.ass03.model

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pcd.ass03.utils.{V2d, v2d}

/** Boid actor protocol.
  */
object BoidActor:

  trait Command
  case class RequestBoid(replyTo: ActorRef[Boid]) extends Command
  case class RequestCalcVelocity(
      boids: Seq[Boid],
      avoidRadius: Double,
      perceptionRadius: Double,
      actorRef: ActorRef[TaskDone]
  ) extends Command
  case class RequestUpdVelocity(
      aliWeight: Double,
      sepWeight: Double,
      cohWeight: Double,
      maxSpeed: Double,
      actorRef: ActorRef[TaskDone]
  ) extends Command
  case class RequestUpdPosition(actorRef: ActorRef[TaskDone]) extends Command
  case class Kill(actorRef: ActorRef[TaskDone]) extends Command

  trait Reply
  case class TaskDone() extends Reply

  def apply(initial: Boid): Behavior[Command] = Behaviors.setup: _ =>
    active(initial)

  private def active(
      boid: Boid,
      sep: V2d = v2d(0),
      ali: V2d = v2d(0),
      coh: V2d = v2d(0)
  ): Behavior[Command] = Behaviors.receive: (cxt, msg) =>
    msg match
      case RequestBoid(ref) =>
        ref ! boid
        Behaviors.same

      case RequestCalcVelocity(boids, avoidRadius, perceptionRadius, ref) =>
        val (sep, ali, coh) =
          calcVelocity(boid, boids, avoidRadius, perceptionRadius)
        ref ! TaskDone()
        active(boid, sep, ali, coh)

      case RequestUpdVelocity(aliW, sepW, cohW, maxSpeed, ref) =>
        val updBoid =
          updVelocity(boid, ali, sep, coh, aliW, sepW, cohW, maxSpeed)
        ref ! TaskDone()
        active(updBoid, sep, ali, coh)

      case RequestUpdPosition(ref) =>
        val updBoid = updPosition(boid)
        ref ! TaskDone()
        active(updBoid, sep, ali, coh)

      case Kill(ref) =>
        ref ! TaskDone()
        Behaviors.stopped

  private def calcVelocity(
      boid: Boid,
      boids: Seq[Boid],
      avoidRadius: Double,
      perceptionRadius: Double
  ): (V2d, V2d, V2d) =
    val neighborhood = boids.filter: b =>
      b != boid && boid.pos.distance(b.pos) < perceptionRadius
    (
      boid.calculateSeparation(neighborhood, avoidRadius),
      boid.calculateAlignment(neighborhood),
      boid.calculateCohesion(neighborhood)
    )

  private def updVelocity(
      boid: Boid,
      ali: V2d,
      sep: V2d,
      coh: V2d,
      aliWeight: Double,
      sepWeight: Double,
      cohWeight: Double,
      maxSpeed: Double
  ): Boid =
    var updBoid = boid.copy(vel =
      boid.vel
        + (ali * aliWeight)
        + (sep * sepWeight)
        + (coh * cohWeight)
    )
    val speed = updBoid.vel.abs
    if speed > maxSpeed then
      updBoid = boid.copy(vel = updBoid.vel.norm * maxSpeed)
    updBoid

  private def updPosition(boid: Boid): Boid =
    boid.copy(pos = boid.pos + boid.vel)
  // TODO: fix below
  /* if (boid.pos.x < model.getMinX)
      boid.pos = boid.pos + V2d(model.width, 0)
    if (boid.pos.x >= model.getMaxX)
      boid.pos = boid.pos + V2d(-model.width, 0)
    if (boid.pos.y < model.getMinY)
      boid.pos = boid.pos + V2d(0, model.height)
    if (boid.pos.y >= model.getMaxY)
      boid.pos = boid.pos + V2d(0, -model.height) */
