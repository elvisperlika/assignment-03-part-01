package pcd.ass03.model

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pcd.ass03.controller.BoidsSimulator.SimulationMessage
import pcd.ass03.controller.BoidsSimulator.SimulationMessage.Reset
import pcd.ass03.utils.P2d
import pcd.ass03.view.{BoidsPanel, BoidsView}

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
    ): Behavior[Commands] = Behaviors.setup: context =>
      Behaviors.setup: context =>
        view.playPauseButton.reactions += {
          case ButtonClicked(_) =>
            if view.playPauseButton.text == "Play" then
              controllerActor ! SimulationMessage.Play
              view.playPauseButton.text = "Pause"
            else
              controllerActor ! SimulationMessage.Pause
              view.playPauseButton.text = "Play"
        }
        view.resetButton.reactions += {
          case ButtonClicked(_) =>
            controllerActor ! SimulationMessage.Reset
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
