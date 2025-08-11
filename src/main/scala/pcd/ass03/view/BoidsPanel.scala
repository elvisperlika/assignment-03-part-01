package pcd.ass03.view

import pcd.ass03.utils.P2d

import java.awt.Color
import scala.swing.{Graphics2D, Panel}

class BoidsPanel extends Panel:

  private var boidsPositions: Seq[P2d] = Seq.empty

  def updatePositions(pos: Seq[P2d]): Unit =
    boidsPositions = pos

  override def paintComponent(g: Graphics2D): Unit =
    super.paintComponent(g)
    g.setColor(Color.BLUE)
    boidsPositions.foreach: p =>
      g.fillOval(p.x.toInt, p.y.toInt, 4, 4)
