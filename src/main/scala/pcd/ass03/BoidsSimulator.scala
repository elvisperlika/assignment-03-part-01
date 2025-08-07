package pcd.ass03

object SimulationParametres:
  val N_BOIDS = 1000

  val SEPARATION_WEIGHT = 1.0
  val ALIGNMENT_WEIGHT = 1.0
  val COHESION_WEIGHT = 1.0

  val ENVIRONMENT_WIDTH = 1000
  val ENVIRONMENT_HEIGHT = 1000
  val MAX_SPEED = 4.0
  val PERCEPTION_RADIUS = 50.0
  val AVOID_RADIUS = 20.0

  val SCREEN_WIDTH = 1400
  val SCREEN_HEIGHT = 600
  
class BoidsSimulator(model: BoidsModel):

  def run(): Unit = println("Running")
