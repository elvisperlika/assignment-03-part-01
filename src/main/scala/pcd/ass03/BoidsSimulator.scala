package pcd.ass03

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}

/** Boids Simulator actor that manage main loop state.
  */
object BoidsSimulator:
  /** Boids Simulator main loop phases.
    */
  enum Loop:
    case Start(nBoids: Int)
    case Play
    case Pause

  enum Command:
    case CalcVelocityDone(id: String)
    case UpdVelocityDone(id: String)
    case UpdPositionDone(id: String)

  def apply(cmd: Loop): Behavior[Loop] =
    cmd match
      case Loop.Start(nBoids) => // TODO
      case Loop.Play          => // TODO
      case Loop.Pause         => // TODO
    Behaviors.same

/** Simulation's controller. If view is available coordinate the model with it.
  * @param model
  *   Contains boids logic.
  */
class BoidsController(
    private val model: BoidsModel,
    private val view: Option[BoidsView] = None
)
