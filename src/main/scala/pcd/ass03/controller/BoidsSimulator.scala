package pcd.ass03.controller

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pcd.ass03.model.ViewActors.DrawMessage.DrawBoids
import pcd.ass03.model.ViewActors.{Commands, Dashboard, DrawMessage, Drawer}
import pcd.ass03.model.{BoidsModel, MainViewActor}
import pcd.ass03.view.BoidsView

object BoidsSimulator:
  // Protocol
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

  // Actor
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

    def running(
        model: BoidsModel,
        drawer: ActorRef[DrawMessage],
        dashboard: ActorRef[Commands],
        paused: Boolean
    ): Behavior[SimulationMessage] =
      Behaviors receive { (context, message) =>
        println(s"Msg: $message")
        Behaviors.same
      }
