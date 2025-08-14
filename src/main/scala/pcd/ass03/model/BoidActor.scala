package pcd.ass03.model

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pcd.ass03.controller.BoidsSimulator.SimulationMessage
import pcd.ass03.utils.P2d
import pcd.ass03.utils.{V2d, v2d}

/** Boid actor protocol.
  */
object BoidActor:
  enum Command:
    case CalcVelocity(
        neighborhood: Seq[Boid],
        avoidRadius: Double,
        replyTo: ActorRef[SimulationMessage]
    )
    case UpdVelocity(replyTo: ActorRef[SimulationMessage])
    case UpdPosition(replyTo: ActorRef[SimulationMessage])
    case Kill(replyTo: ActorRef[SimulationMessage])
    case RequestBoid(replyTo: ActorRef[Boid])

  def apply(initial: Boid): Behavior[Command] = Behaviors.setup: _ =>
    active(initial)

  def active(
      boid: Boid,
      sep: V2d = v2d(0),
      ali: V2d = v2d(0),
      coh: V2d = v2d(0)
  ): Behavior[Command] = Behaviors.receive: (cxt, msg) =>
    msg match
      case Command.CalcVelocity(neighborhood, avoidRadius, replyTo) =>
        val (sep, ali, coh) = calcVelocity(boid, neighborhood, avoidRadius)
        active(boid, sep, ali, coh)
      case Command.UpdVelocity(replyTo) => Behaviors.same
      case Command.UpdPosition(replyTo) => Behaviors.same
      case Command.Kill(replyTo)        => Behaviors.same
      case Command.RequestBoid(replyTo) =>
        replyTo ! boid
        Behaviors.same

  /* def apply(boid: Boid): Behavior[Task] = Behaviors.receive: (cxt, msg) =>
    import Task.*
    msg match
      case CalcVelocity(model, replyTo) => calcVelocity(model, boid)
      case UpdVelocity(model, replyTo)  => updVelocity(model, boid)
      case UpdPosition(model, replyTo)  => updPosition(model, boid)
      case Kill(replyTo)                => Behaviors.stopped
    Behaviors.same */

  private def calcVelocity(
      boid: Boid,
      neighborhood: Seq[Boid],
      avoidRadius: Double
  ): (V2d, V2d, V2d) =
//    val neighborhood = model.boids.filter: b =>
//      b != boid && boid.pos.distance(b.pos) < model.perceptionRadius
    (
      boid.calculateSeparation(neighborhood, avoidRadius),
      boid.calculateAlignment(neighborhood),
      boid.calculateCohesion(neighborhood)
    )

  /* private def updVelocity(boid: Boid, ali: Double, sep: Double, coh: Double, maxSpeed: Double): Boid =
    val updBoid = boid.copy(vel =
      boid.vel
        + (boid.alignment * ali)
        + (boid.separation * sep)
        + (boid.cohesion * coh))
    val speed = updBoid.vel.abs
    if speed > maxSpeed then
      boid.copy(vel = updBoid.vel.norm * maxSpeed)
    

  private def updPosition(model: BoidsModel, boid: Boid): Unit =
    boid.pos = boid.pos + boid.vel */
  // TODO: fix below
  /* if (boid.pos.x < model.getMinX)
      boid.pos = boid.pos + V2d(model.width, 0)
    if (boid.pos.x >= model.getMaxX)
      boid.pos = boid.pos + V2d(-model.width, 0)
    if (boid.pos.y < model.getMinY)
      boid.pos = boid.pos + V2d(0, model.height)
    if (boid.pos.y >= model.getMaxY)
      boid.pos = boid.pos + V2d(0, -model.height) */
