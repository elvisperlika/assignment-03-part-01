package pcd.ass03

import scala.swing.*

class BoidsView extends MainFrame:
  title = "Boids Simulation"
  import SimulationParameters.*
  preferredSize = new Dimension(ENVIRONMENT_WIDTH, ENVIRONMENT_HEIGHT)

  val drawablePanel = new Panel:
    background = java.awt.Color.WHITE

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

  contents = new BorderPanel:
    layout(drawablePanel) = BorderPanel.Position.Center
    layout(new FlowPanel(
      playPauseButton,
      nBoidsField,
      new Label("Separation"),
      separtionSlider,
      new Label("Alignment"),
      alignmentSlider,
      new Label("Cohesion"),
      cohesionSlider,
      resetButton
    )) =
      BorderPanel.Position.South
