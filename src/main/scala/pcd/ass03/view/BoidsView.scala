package pcd.ass03.view

import scala.swing.*

class BoidsView(width: Int, height: Int) extends MainFrame:
  title = "Boids Simulation"
  preferredSize = new Dimension(width, height)

  val drawablePanel: BoidsPanel = new BoidsPanel

  val playPauseButton = new Button("Play")

  val nBoidsField: TextField = new TextField:
    columns = 7

  private def makeSlider(): Slider = new Slider:
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
