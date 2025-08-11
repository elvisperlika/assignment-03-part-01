package pcd.ass03

import akka.actor.typed.ActorSystem
import pcd.ass03.model.BoidsModel
import pcd.ass03.controller.BoidsSimulator.ControllerActor
import pcd.ass03.view.BoidsView

import scala.swing.{Frame, SimpleSwingApplication}

object BoidsApp extends SimpleSwingApplication:

  import pcd.ass03.utils.SimulationParameters.*
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
  val system = ActorSystem(ControllerActor(model, view), "sim-controller")
  def top: Frame = view
