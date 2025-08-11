package pcd.ass03

import scala.swing.{Frame, SimpleSwingApplication}

object BoidsApp extends SimpleSwingApplication:

  import SimulationParameters.*
  val model = BoidsModel(
    DEFAULT_N_BOIDS,
    SEPARATION_WEIGHT,
    ALIGNMENT_WEIGHT,
    COHESION_WEIGHT,
    ENVIRONMENT_WIDTH,
    ENVIRONMENT_HEIGHT,
    MAX_SPEED,
    PERCEPTION_RADIUS,
    AVOID_RADIUS
  )
  val view = new BoidsView()
  val controller = new BoidsController(model, Some(view))

  def top: Frame = view
