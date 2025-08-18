package pcd.ass03.model

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pcd.ass03.controller.BoidsSimulator.{
  Pause,
  Play,
  Reset,
  SetBoidSize,
  SimulationPhase,
  UpdateParameters
}
import pcd.ass03.utils.P2d
import pcd.ass03.view.{BoidsPanel, BoidsView}

import java.awt.Color
import scala.swing.event.{ButtonClicked, EditDone, Event, ValueChanged}
import scala.swing.Swing

object ViewActors:

  /** Actor that get commands from the user and send to the controller.
    */
  object Dashboard:
    def apply(
        view: BoidsView,
        controllerActor: ActorRef[SimulationPhase]
    ): Behavior[Nothing] = Behaviors.setup: context =>
      Behaviors.setup: context =>
        view.playPauseButton.reactions += {
          case ButtonClicked(_) =>
            if view.playPauseButton.text == "Play" then
              controllerActor ! Play()
              view.playPauseButton.text = "Pause"
            else
              controllerActor ! Pause()
              view.playPauseButton.text = "Play"
        }
        view.resetButton.reactions += {
          case ButtonClicked(_) =>
            view.playPauseButton.text = "Pause"
            controllerActor ! SetBoidSize(view.nBoidsField.text.toInt)
            controllerActor ! Reset()
        }

        val sliderReaction: PartialFunction[Event, Unit] = {
          case ValueChanged(_) =>
            controllerActor ! UpdateParameters(
              separation = view.separationSlider.value,
              alignment = view.alignmentSlider.value,
              cohesion = view.cohesionSlider.value
            )
        }

        view.separationSlider.reactions += sliderReaction
        view.alignmentSlider.reactions += sliderReaction
        view.cohesionSlider.reactions += sliderReaction

        Behaviors.empty

  trait DrawMessage
  case class DrawBoids(positions: Seq[P2d]) extends DrawMessage

  /** Actor that draw the simulation on the view.
    */
  object Drawer:
    def apply(panel: BoidsPanel): Behavior[DrawMessage] =
      Behaviors.receive: (context, message) =>
        message match
          case DrawBoids(positions) =>
            panel.updatePositions(positions)
            Swing.onEDT { panel.repaint() }
        Behaviors.same
