package pcd.ass03

/** Boid is primary entity of the simulation.
  * @param _pos
  *   is its position.
  * @param _vel
  *   is its velocity.
  */
class Boid(private var _pos: P2d, private var _vel: V2d):
  def pos: P2d = _pos
  def pos_=(newPos: P2d): Unit = _pos = newPos
  def vel: V2d = _vel
  def vel_=(newVel: V2d): Unit = _vel = newVel
