package pcd.ass03.model

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.ActorContext
import pcd.ass03.controller.BoidsSimulator.SimulationPhase
import pcd.ass03.model.BoidActor.Command
import pcd.ass03.utils.{P2d, V2d}

class BoidsModel(
    var nBoids: Int,
    var separationWeight: Double,
    var alignmentWeight: Double,
    var cohesionWeight: Double,
    val width: Int,
    val height: Int,
    val maxSpeed: Double,
    val perceptionRadius: Double,
    val avoidRadius: Double
):
  var boids: Seq[Boid] = Seq()
  var boidsRef: Seq[ActorRef[Command]] = Seq()

  def generateBoids(context: ActorContext[SimulationPhase]): Unit =
    boids =
      for
        i <- 0 until nBoids
        pos = P2d(0 + Math.random * width, 0 + Math.random * height)
        vel = V2d(
          Math.random * maxSpeed / 2 - maxSpeed / 4,
          Math.random * maxSpeed / 2 - maxSpeed / 4
        )
      yield Boid(pos, vel)
    boidsRef =
      for
        (b, i) <- boids.zipWithIndex
      yield context spawn (BoidActor(b), s"boid-$i")

  def getMinX: Double = -width / 2
  
  def getMaxX: Double = width / 2
  
  def getMinY: Double = -height / 2
  
  def getMaxY: Double = height / 2

  def setBoidsNumber(n: Int): Unit = nBoids = n
