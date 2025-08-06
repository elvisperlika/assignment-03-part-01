package simulation

case class Boid(private var _pos: P2d, private var _vel: V2d):
  def pos: P2d = _pos
  def pos_=(newPos: P2d): Unit = _pos = newPos
  def vel: V2d = _vel
  def vel_=(newVel: V2d): Unit = _vel = newVel
