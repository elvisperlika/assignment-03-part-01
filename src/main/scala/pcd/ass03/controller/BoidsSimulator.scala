package pcd.ass03.controller

import akka.actor.typed.scaladsl.AskPattern.*
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pcd.ass03.model.BoidActor.BoidTask
import pcd.ass03.model.BoidsModel
import pcd.ass03.model.ViewActors.DrawMessage.DrawBoids
import pcd.ass03.model.ViewActors.{Commands, Dashboard, DrawMessage, Drawer}
import pcd.ass03.view.BoidsView

object BoidsSimulator:
  enum SimulationMessage:
    case UpdateParameters(
        separation: Int,
        alignment: Int,
        cohesion: Int,
        nBoids: Int
    )
    case Play
    case Pause
    case Reset
    case Tick

  object ControllerActor:
    def apply(
        model: BoidsModel,
        view: BoidsView
    ): Behavior[SimulationMessage] = Behaviors.setup: context =>
      model.generateBoids(context)
      val drawer = context.spawn(Drawer(view.drawablePanel), "drawer")
      val dashboard = context.spawn(Dashboard(view, context.self), "dashboard")
      drawer ! DrawMessage.DrawBoids(model.boids.map(_.pos))
      running(model, drawer, dashboard, true)

    private def running(
        model: BoidsModel,
        drawer: ActorRef[DrawMessage],
        dashboard: ActorRef[Commands],
        paused: Boolean
    ): Behavior[SimulationMessage] =
      Behaviors receive: (context, message) =>
        message match
          case SimulationMessage.Play if paused =>
            println("Play")
            context.self ! SimulationMessage.Tick
            running(model, drawer, dashboard, paused = false)

          case SimulationMessage.Tick if !paused =>
            println("Tick")
            model.boidsRef.foreach: b =>
              b ! BoidTask.CalcVelocity(model, context.self)
            model.boidsRef.foreach: b =>
              b ! BoidTask.UpdVelocity(model, context.self)
            model.boidsRef.foreach: b =>
              b ! BoidTask.UpdPosition(model, context.self)
            drawer ! DrawBoids(model.boids.map(_.pos))
            context.self ! SimulationMessage.Tick
            Behaviors.same

          case SimulationMessage.Pause if !paused =>
            println("Pause")
            running(model, drawer, dashboard, paused = true)

          case SimulationMessage.UpdateParameters(_, _, _, _) =>
            println("Update Parameters")
            Behaviors.same

          case SimulationMessage.Reset =>
            println("Reset")
            model.boidsRef.foreach: b =>
              b ! BoidTask.Kill
            model.generateBoids(context)
            drawer ! DrawMessage.DrawBoids(model.boids.map(_.pos))
            Behaviors.same

          case _ =>
            Behaviors.same
