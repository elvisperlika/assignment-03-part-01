package pcd.ass03.view

import pcd.ass03.utils.SimulationParameters.{
  ENVIRONMENT_HEIGHT,
  ENVIRONMENT_WIDTH
}
import pcd.ass03.utils.SimulationParameters

import scala.swing.*

class BoidsView extends MainFrame:
  title = "Boids Simulation"
  import SimulationParameters.*
  preferredSize = new Dimension(ENVIRONMENT_WIDTH, ENVIRONMENT_HEIGHT)

  val drawablePanel: BoidsPanel = new BoidsPanel

  val playPauseButton = new Button("Play")

  val nBoidsField = new TextField:
    columns = 7

  def makeSlider(): Slider = new Slider:
    min = 0
    max = 20
    value = 10

  val separtionSlider: Slider = makeSlider()
  val alignmentSlider: Slider = makeSlider()
  val cohesionSlider: Slider = makeSlider()

  val resetButton = new Button("Reset")

  val commandPanel = new FlowPanel(
    playPauseButton,
    nBoidsField,
    new Label("Separation"),
    separtionSlider,
    new Label("Alignment"),
    alignmentSlider,
    new Label("Cohesion"),
    cohesionSlider,
    resetButton
  )

  contents = new BorderPanel:
    layout(drawablePanel) = BorderPanel.Position.Center
    layout(commandPanel) = BorderPanel.Position.South
