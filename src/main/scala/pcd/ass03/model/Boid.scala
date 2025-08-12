package pcd.ass03.model

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pcd.ass03.V2dJ
import pcd.ass03.controller.BoidsSimulator.SimulationMessage
import pcd.ass03.utils.{P2d, V2d, v2d}

class Boid(
    var pos: P2d,
    var vel: V2d,
    var separation: V2d = v2d(0),
    var alignment: V2d = v2d(0),
    var cohesion: V2d = v2d(0)
):
  def calculateAlignment(nearbyBoids: Seq[Boid], model: BoidsModel): V2d =
    if (nearbyBoids.nonEmpty) {
      val (sumVx, sumVy) =
        nearbyBoids.foldLeft((0.0, 0.0)) { case ((accX, accY), other) =>
          val otherVel = other.vel
          (accX + otherVel.x, accY + otherVel.y)
        }
      val avgVx = sumVx / nearbyBoids.size
      val avgVy = sumVy / nearbyBoids.size
      V2d(avgVx - vel.x, avgVy - vel.y).norm
    } else
      V2d(0, 0)

  def calculateCohesion(nearbyBoids: Seq[Boid], model: BoidsModel): V2d =
    if (nearbyBoids.nonEmpty) {
      val (centerX, centerY) =
        nearbyBoids.foldLeft((0.0, 0.0)) { case ((accX, accY), other) =>
          val otherPos = other.pos
          (accX + otherPos.x, accY + otherPos.y)
        }
      val avgX = centerX / nearbyBoids.size
      val avgY = centerY / nearbyBoids.size
      V2d(avgX - pos.x, avgY - pos.y).norm
    } else
      V2d(0, 0)

  def calculateSeparation(nearbyBoids: Seq[Boid], model: BoidsModel): V2d =
    val (dx, dy, count) = nearbyBoids.foldLeft((0.0, 0.0, 0)) {
      case ((accX, accY, accCount), other) =>
        val otherPos = other.pos
        val distance = pos.distance(otherPos)
        if (distance < model.avoidRadius) {
          (
            accX + pos.x - otherPos.x,
            accY + pos.y - otherPos.y,
            accCount + 1
          )
        } else
          (accX, accY, accCount)
    }
    if count > 0 then V2d(dx / count, dy / count).norm
    else V2d(0, 0)

/** Boid actor protocol.
  */
object BoidActor:
  enum BoidTask:
    case Idle
    case CalcVelocity(model: BoidsModel, replyTo: ActorRef[SimulationMessage])
    case UpdVelocity(model: BoidsModel, replyTo: ActorRef[SimulationMessage])
    case UpdPosition(model: BoidsModel, replyTo: ActorRef[SimulationMessage])
    case Kill

  def apply(boid: Boid): Behavior[BoidTask] = Behaviors.receive: (cxt, msg) =>
    import BoidTask.*
    msg match
      case CalcVelocity(model, replyTo) => calcVelocity(model, boid)
      case UpdVelocity(model, replyTo)  => updVelocity(model, boid)
      case UpdPosition(model, replyTo)  => updPosition(model, boid)
      case Kill                         => Behaviors.stopped
    Behaviors.same

  private def calcVelocity(model: BoidsModel, boid: Boid): Unit =
    // println("calcVelocity")
    val neighborhood = model.boids.filter: b =>
      b != boid && boid.pos.distance(b.pos) < model.perceptionRadius
    boid.separation = boid.calculateSeparation(neighborhood, model)
    boid.alignment = boid.calculateAlignment(neighborhood, model)
    boid.cohesion = boid.calculateCohesion(neighborhood, model)

  private def updVelocity(model: BoidsModel, boid: Boid): Unit =
    // println("updVelocity")
    boid.vel =
      boid.vel + (boid.alignment * (
        model.alignmentWeight
      )) + (boid.separation * (
        model.separationWeight
      )) + (boid.cohesion * (
        model.cohesionWeight
      ))
    val speed = boid.vel.abs
    if speed > model.maxSpeed then
      boid.vel = boid.vel.norm * (model.maxSpeed)

  private def updPosition(model: BoidsModel, boid: Boid): Unit =
    // println("updPosition")
    boid.pos = boid.pos + (boid.vel)
    if (boid.pos.x < model.getMinX)
      boid.pos = boid.pos + V2d(model.width, 0)
    if (boid.pos.x >= model.getMaxX)
      boid.pos = boid.pos + V2d(-model.width, 0)
    if (boid.pos.y < model.getMinY)
      boid.pos = boid.pos + V2d(0, model.height)
    if (boid.pos.y >= model.getMaxY)
      boid.pos = boid.pos + V2d(0, -model.height)
