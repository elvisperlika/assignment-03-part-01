package pcd.ass03.model

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pcd.ass03.controller.BoidsSimulator.SimulationMessage
import pcd.ass03.utils.P2d
import pcd.ass03.view.{BoidsPanel, BoidsView}

import java.awt.Graphics2D
import scala.swing.{Panel, Swing}
import scala.swing.event.ButtonClicked

object ViewActors:

  enum Commands:
    case Play
    case Pause
    case Reset
    case SliderChange(separation: Int, alignment: Int, cohesion: Int)
    case NumBoidsChanged(n: Int)

  object Dashboard:
    def apply(
        view: BoidsView,
        controllerActor: ActorRef[SimulationMessage]
    ): Behavior[Commands] =
      view.playPauseButton.reactions += {
        case ButtonClicked(_) =>

          controllerActor ! SimulationMessage.Play
      }

      Behaviors.empty

  import scala.swing.Swing
  enum DrawMessage:
    case DrawBoids(positions: Seq[P2d])

  object Drawer:
    def apply(panel: BoidsPanel): Behavior[DrawMessage] =
      Behaviors.receive: (context, message) =>
        message match
          case DrawMessage.DrawBoids(positions) =>
            panel.updatePositions(positions)
            Swing.onEDT { panel.repaint() }
        Behaviors.same

object MainViewActor:
  import ViewActors.Drawer
  import ViewActors.Dashboard
  def apply(
      view: BoidsView,
      controllerActor: ActorRef[SimulationMessage]
  ): Behavior[Nothing] = Behaviors.setup: context =>
    val drawer = context.spawn(Drawer(view.drawablePanel), "drawer")
    val dashboard = context.spawn(Dashboard(view, controllerActor), "dashboard")
    Behaviors.empty

