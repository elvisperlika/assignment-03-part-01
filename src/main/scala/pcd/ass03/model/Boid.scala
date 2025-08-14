package pcd.ass03.model

import pcd.ass03.utils.{P2d, V2d}

case class Boid(
    pos: P2d,
    vel: V2d
):
  def calculateAlignment(nearbyBoids: Seq[Boid]): V2d =
    if nearbyBoids.nonEmpty then
      val (sumVx, sumVy) =
        nearbyBoids.foldLeft((0.0, 0.0)) { case ((accX, accY), other) =>
          val otherVel = other.vel
          (accX + otherVel.x, accY + otherVel.y)
        }
      val avgVx = sumVx / nearbyBoids.size
      val avgVy = sumVy / nearbyBoids.size
      V2d(avgVx - vel.x, avgVy - vel.y).norm
    else V2d(0, 0)

  def calculateCohesion(nearbyBoids: Seq[Boid]): V2d =
    if nearbyBoids.nonEmpty then
      val (centerX, centerY) =
        nearbyBoids.foldLeft((0.0, 0.0)) { case ((accX, accY), other) =>
          val otherPos = other.pos
          (accX + otherPos.x, accY + otherPos.y)
        }
      val avgX = centerX / nearbyBoids.size
      val avgY = centerY / nearbyBoids.size
      V2d(avgX - pos.x, avgY - pos.y).norm
    else V2d(0, 0)

  def calculateSeparation(nearbyBoids: Seq[Boid], avoidRadius: Double): V2d =
    val (dx, dy, count) = nearbyBoids.foldLeft((0.0, 0.0, 0)) {
      case ((accX, accY, accCount), other) =>
        val distance = pos.distance(other.pos)
        if distance < avoidRadius then
          (accX + pos.x - other.pos.x, accY + pos.y - other.pos.y, accCount + 1)
        else (accX, accY, accCount)
    }
    if count > 0 then V2d(dx / count, dy / count).norm
    else V2d(0, 0)
