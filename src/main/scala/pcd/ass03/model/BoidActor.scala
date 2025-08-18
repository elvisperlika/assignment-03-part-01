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
      sepWeight: Double,
      aliWeight: Double,
      cohWeight: Double,
      maxSpeed: Double,
      actorRef: ActorRef[TaskDone]
  ) extends Command
  case class RequestUpdPosition(
      width: Double,
      height: Double,
      actorRef: ActorRef[TaskDone]
  ) extends Command
  case class Kill() extends Command

  trait Reply
  case class TaskDone() extends Reply

  def apply(initial: Boid): Behavior[Command] = Behaviors.setup: _ =>
    active(initial, v2d(0), v2d(0), v2d(0))

  private type Separation = V2d
  private type Alignment = V2d
  private type Cohesion = V2d

  private def active(
      _boid: Boid,
      _sep: Separation,
      _ali: Alignment,
      _coh: Cohesion
  ): Behavior[Command] = Behaviors.receive: (cxt, msg) =>
    msg match
      case RequestBoid(ref) =>
        ref ! _boid
        Behaviors.same

      case RequestCalcVelocity(boids, avoidRadius, perceptionRadius, ref) =>
        val (newSep, newAli, newCoh) =
          calcVelocity(_boid, boids, avoidRadius, perceptionRadius)
        ref ! TaskDone()
        active(_boid, newSep, newAli, newCoh)

      case RequestUpdVelocity(sepW, aliW, cohW, maxSpeed, ref) =>
        val updBoid =
          updVelocity(_boid, _sep, _ali, _coh, sepW, aliW, cohW, maxSpeed)
        ref ! TaskDone()
        active(updBoid, _sep, _ali, _coh)

      case RequestUpdPosition(width, height, ref) =>
        val updBoid = updPosition(width, height, _boid)
        ref ! TaskDone()
        active(updBoid, _sep, _ali, _coh)

      case Kill() => Behaviors.stopped

  private def calcVelocity(
      boid: Boid,
      boids: Seq[Boid],
      avoidRadius: Double,
      perceptionRadius: Double
  ): (Separation, Alignment, Cohesion) =
    val neighborhood = boids.filter: b =>
      b.id != boid.id && boid.pos.distance(b.pos) < perceptionRadius
    (
      boid.calculateSeparation(neighborhood, avoidRadius),
      boid.calculateAlignment(neighborhood),
      boid.calculateCohesion(neighborhood)
    )

  private def updVelocity(
      boid: Boid,
      sep: Separation,
      ali: Alignment,
      coh: Cohesion,
      sepWeight: Double,
      aliWeight: Double,
      cohWeight: Double,
      maxSpeed: Double
  ): Boid =
    var updBoid = boid.copy(vel =
      boid.vel + (ali * aliWeight) + (sep * sepWeight) + (coh * cohWeight)
    )
    val speed = updBoid.vel.abs
    if speed > maxSpeed then
      updBoid = updBoid.copy(vel = updBoid.vel.norm * maxSpeed)
    updBoid

  private def updPosition(width: Double, height: Double, boid: Boid): Boid =
    var updBoid = boid.copy(pos = boid.pos + boid.vel)
    if (updBoid.pos.x < 0)
      updBoid = updBoid.copy(updBoid.pos + V2d(width, 0))
    if (updBoid.pos.x >= width)
      updBoid = updBoid.copy(updBoid.pos + V2d(-width, 0))
    if (updBoid.pos.y < 0)
      updBoid = updBoid.copy(updBoid.pos + V2d(0, height))
    if (updBoid.pos.y >= height)
      updBoid = updBoid.copy(updBoid.pos + V2d(0, -height))
    updBoid
