package pcd.ass03.model

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import pcd.ass03.controller.BoidsSimulator.SimulationMessage
import pcd.ass03.utils.V2d

/** Boid actor protocol.
  */
object BoidActor:
  enum BoidTask:
    case CalcVelocity(model: BoidsModel, replyTo: ActorRef[SimulationMessage])
    case UpdVelocity(model: BoidsModel, replyTo: ActorRef[SimulationMessage])
    case UpdPosition(model: BoidsModel, replyTo: ActorRef[SimulationMessage])
    case Kill(replyTo: ActorRef[SimulationMessage])

  def apply(boid: Boid): Behavior[BoidTask] = Behaviors.receive: (cxt, msg) =>
    import BoidTask.*
    msg match
      case CalcVelocity(model, replyTo) => calcVelocity(model, boid)
      case UpdVelocity(model, replyTo)  => updVelocity(model, boid)
      case UpdPosition(model, replyTo)  => updPosition(model, boid)
      case Kill(replyTo)                => Behaviors.stopped
    Behaviors.same

  private def calcVelocity(model: BoidsModel, boid: Boid): Unit =
    val neighborhood = model.boids.filter: b =>
      b != boid && boid.pos.distance(b.pos) < model.perceptionRadius
    boid.separation = boid.calculateSeparation(neighborhood, model)
    boid.alignment = boid.calculateAlignment(neighborhood, model)
    boid.cohesion = boid.calculateCohesion(neighborhood, model)

  private def updVelocity(model: BoidsModel, boid: Boid): Unit =
    boid.vel =
      boid.vel
        + (boid.alignment * model.alignmentWeight)
        + (boid.separation * model.separationWeight)
        + (boid.cohesion * model.cohesionWeight)
    val speed = boid.vel.abs
    if speed > model.maxSpeed then
      boid.vel = boid.vel.norm * model.maxSpeed

  private def updPosition(model: BoidsModel, boid: Boid): Unit =
    boid.pos = boid.pos + boid.vel
    // TODO: fix below
    /* if (boid.pos.x < model.getMinX)
      boid.pos = boid.pos + V2d(model.width, 0)
    if (boid.pos.x >= model.getMaxX)
      boid.pos = boid.pos + V2d(-model.width, 0)
    if (boid.pos.y < model.getMinY)
      boid.pos = boid.pos + V2d(0, model.height)
    if (boid.pos.y >= model.getMaxY)
      boid.pos = boid.pos + V2d(0, -model.height) */
