package pcd.ass03.model

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pcd.ass03.utils.{P2d, V2d}

/** Boid is primary entity of the simulation.
  *
  * @param _pos
  *   is its position.
  * @param _vel
  *   is its velocity.
  */
class Boid(private var _pos: P2d, private var _vel: V2d):
  def pos: P2d = _pos
  def pos_=(newPos: P2d): Unit = _pos = newPos
  def vel: V2d = _vel
  def vel_=(newVel: V2d): Unit = _vel = newVel

/** Boid actor protocol.
  */
object BoidActor:
  enum BoidTask:
    case Idle
    case CalcVelocity(model: BoidsModel, replyTo: ActorRef[BoidActor.BoidTask])
    case UpdVelocity(model: BoidsModel, replyTo: ActorRef[BoidActor.BoidTask])
    case UpdPosition(model: BoidsModel, replyTo: ActorRef[BoidActor.BoidTask])

  /** Initial state of a Boid.
    * @param boid
    * @return
    */
  def apply(boid: Boid): Behavior[BoidTask] = Behaviors.receive: (cxt, msg) =>
    import BoidTask.*
    msg match
      case CalcVelocity(model, replyTo) => calcVelocity(model, boid)
      case UpdVelocity(model, replyTo)  => updVelocity(model, boid)
      case UpdPosition(model, replyTo)  => updPosition(model, boid)

  private def calcVelocity(model: BoidsModel, boid: Boid): Behavior[BoidTask] =
    val neighborhood = model.boids.filter: b =>
      b != boid && boid.pos.distance(b.pos) < model.perceptionRadius
    // TODO
    Behaviors.same

  private def updVelocity(model: BoidsModel, boid: Boid): Behavior[BoidTask] =
    // TODO
    Behaviors.same

  private def updPosition(model: BoidsModel, boid: Boid): Behavior[BoidTask] =
    // TODO
    Behaviors.same
