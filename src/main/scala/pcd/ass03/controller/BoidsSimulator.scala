package pcd.ass03.controller

import akka.actor.typed.scaladsl.AskPattern.*
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, Scheduler}
import akka.util.Timeout
import pcd.ass03.model.BoidActor.{
  Kill,
  RequestBoid,
  RequestCalcVelocity,
  RequestUpdPosition,
  RequestUpdVelocity,
  TaskDone
}
import pcd.ass03.model.ViewActors.{
  Commands,
  Dashboard,
  DrawBoids,
  DrawBoidsWithResponse,
  DrawMessage,
  Drawer,
  Drew
}
import pcd.ass03.model.{Boid, BoidsModel}
import pcd.ass03.view.BoidsView

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object BoidsSimulator:
  trait SimulationPhase
  case class UpdateParameters(separation: Int, alignment: Int, cohesion: Int)
      extends SimulationPhase
  case class Play() extends SimulationPhase
  case class Pause() extends SimulationPhase
  case class Reset() extends SimulationPhase
  private case class Tick() extends SimulationPhase

  object ControllerActor:
    def apply(
        model: BoidsModel,
        view: BoidsView
    ): Behavior[SimulationPhase] = Behaviors.setup: context =>
      model.generateBoids(context)
      val drawer = context.spawn(Drawer(view.drawablePanel), "drawer")
      val dashboard = context.spawn(Dashboard(view, context.self), "dashboard")
      drawer ! DrawBoids(model.boids.map(_.pos))
      running(model, drawer, dashboard, true)

    private def running(
        model: BoidsModel,
        drawer: ActorRef[DrawMessage],
        dashboard: ActorRef[Commands],
        paused: Boolean
    ): Behavior[SimulationPhase] = Behaviors receive: (context, message) =>
      message match
        case Play() if paused =>
          context.self ! Tick()
          running(model, drawer, dashboard, paused = false)

        case Tick() if !paused =>
          given Timeout = 3.seconds
          given Scheduler = context.system.scheduler
          given ExecutionContext = context.executionContext

          /* REQUEST CALCULATION VELOCITIES */
          val futureCalcVelocityTask: Seq[Future[TaskDone]] =
            model.boidsRef.map: b =>
              val boids: Seq[Boid] = Seq.from(model.boids)
              b.ask(replyTo =>
                RequestCalcVelocity(
                  boids,
                  model.avoidRadius,
                  model.perceptionRadius,
                  replyTo
                )
              )
          val allFutureCalcVelocityTask: Future[Seq[TaskDone]] =
            Future.sequence(futureCalcVelocityTask)
          allFutureCalcVelocityTask.onComplete {
            case Failure(exception) => println(s"ERROR: $exception")
            case _                  =>
          }

          /* REQUEST UPDATE VELOCITIES */
          val futureUpdVelocityTask: Seq[Future[TaskDone]] =
            model.boidsRef.map: b =>
              val boids: Seq[Boid] = Seq.from(model.boids)
              b.ask(replyTo =>
                RequestUpdVelocity(
                  model.alignmentWeight,
                  model.separationWeight,
                  model.cohesionWeight,
                  model.maxSpeed,
                  replyTo
                )
              )
          val allFutureUpdVelocityTask: Future[Seq[TaskDone]] =
            Future.sequence(futureUpdVelocityTask)
          allFutureCalcVelocityTask.onComplete {
            case Failure(exception) => println(s"ERROR: $exception")
            case _                  =>
          }

          /* REQUEST UPDATE POSITIONS */
          val futureUpdPositionTask: Seq[Future[TaskDone]] =
            model.boidsRef.map: b =>
              b.ask(replyTo => RequestUpdPosition(replyTo))
          val allFutureUpdPositionTask: Future[Seq[TaskDone]] =
            Future.sequence(futureUpdPositionTask)
          allFutureUpdPositionTask.onComplete {
            case Failure(exception) => println(s"ERROR: $exception")
            case Success(value)     =>
          }

          /* UPDATE POSITIONS */
          val futureBoids: Seq[Future[Boid]] = model.boidsRef.map: b =>
            b.ask(replyTo => RequestBoid(replyTo))
          val allfutureBoids: Future[Seq[Boid]] = Future.sequence(futureBoids)
          allfutureBoids.onComplete {
            case Failure(exception) => println(s"ERROR: $exception")
            case Success(boids)     => model.boids = boids
          }

          val futureDraw: Future[Drew] =
            drawer.ask(replyTo =>
              DrawBoidsWithResponse(model.boids.map(_.pos), replyTo)
            )
          futureDraw.onComplete {
            case Failure(exception) => println(s"ERROR $exception")
            case _                  => context.self ! Tick()
          }

          Behaviors.same

        case Pause() if !paused =>
          running(model, drawer, dashboard, paused = true)

        case UpdateParameters(sep, ali, coh) =>
          model.separationWeight = sep
          model.alignmentWeight = ali
          model.cohesionWeight = coh
          Behaviors.same

        case Reset() =>
//          model.boidsRef foreach: b =>
//            b ! Kill(context.self)
          model generateBoids context
          drawer ! DrawBoids(model.boids.map(_.pos))
          Behaviors.same

        case _ =>
          Behaviors.same
