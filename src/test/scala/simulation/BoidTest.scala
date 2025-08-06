package simulation

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class BoidTest extends AnyFlatSpec with should.Matchers:
  
  "Boid" should "set pos" in:
    val boid: Boid = Boid(P2d(0, 0), V2d(0, 0))
    boid.pos = P2d(1, 1)
    boid shouldEqual Boid(P2d(1, 1), V2d(0, 0))

  it should "set vel" in:
    val boid: Boid = Boid(P2d(0, 0), V2d(0, 0))
    boid.vel = V2d(1, 1)
    boid shouldEqual Boid(P2d(0, 0), V2d(1, 1))