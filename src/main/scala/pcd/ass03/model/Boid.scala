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

