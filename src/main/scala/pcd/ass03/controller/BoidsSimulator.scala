package pcd.ass03.controller

import akka.actor.typed.scaladsl.AskPattern.*
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, Scheduler}
import akka.util.Timeout
import pcd.ass03.model.BoidActor.*
import pcd.ass03.model.ViewActors.*
import pcd.ass03.model.{Boid, BoidsModel}
import pcd.ass03.view.BoidsView

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object BoidsSimulator:
  trait SimulationPhase
  case class UpdateParameters(
      separation: Double,
      alignment: Double,
      cohesion: Double
  ) extends SimulationPhase
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
      running(model, drawer, dashboard, paused = true)

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

          val simulationStep =
            for
              _ <- requestCalculateVelocities(model)
              _ <- requestUpdateVelocities(model)
              _ <- requestUpdatePositions(model)
              boids <- updateModelBoids(model)
              _ = model.boids = boids
            yield ()

          simulationStep.onComplete {
            case Failure(exception) => println(s"ERROR: $exception")
            case Success(_)         =>
              drawer ! DrawBoids(model.boids.map(_.pos))
              context.scheduleOnce(16.millis, context.self, Tick())
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
          model.boidsRef.foreach(_ ! Kill())
          if model.boidsRef.isEmpty then
            model generateBoids context
            drawer ! DrawBoids(model.boids.map(_.pos))
          Behaviors.same

        case _ =>
          Behaviors.same

private def requestCalculateVelocities(model: BoidsModel)(using
    Timeout,
    Scheduler,
    ExecutionContext
): Future[Seq[TaskDone]] =
  val tasks: Seq[Future[TaskDone]] = model.boidsRef.map: b =>
    val boids = Seq.from(model.boids)
    b.ask(replyTo =>
      RequestCalcVelocity(
        boids,
        model.avoidRadius,
        model.perceptionRadius,
        replyTo
      )
    )
  Future.sequence(tasks)

private def requestUpdateVelocities(model: BoidsModel)(using
    Timeout,
    Scheduler,
    ExecutionContext
): Future[Seq[TaskDone]] =
  val tasks: Seq[Future[TaskDone]] = model.boidsRef.map: b =>
    val boids = Seq.from(model.boids)
    b.ask(replyTo =>
      RequestUpdVelocity(
        model.separationWeight,
        model.alignmentWeight,
        model.cohesionWeight,
        model.maxSpeed,
        replyTo
      )
    )
  Future.sequence(tasks)

private def requestUpdatePositions(model: BoidsModel)(using
    Timeout,
    Scheduler,
    ExecutionContext
): Future[Seq[TaskDone]] =
  val tasks: Seq[Future[TaskDone]] = model.boidsRef.map: b =>
    b.ask(replyTo => RequestUpdPosition(model.width, model.height, replyTo))
  Future.sequence(tasks)

private def updateModelBoids(model: BoidsModel)(using
    Timeout,
    Scheduler,
    ExecutionContext
): Future[Seq[Boid]] =
  val tasks: Seq[Future[Boid]] = model.boidsRef.map: b =>
    b.ask(replyTo => RequestBoid(replyTo))
  Future.sequence(tasks)
