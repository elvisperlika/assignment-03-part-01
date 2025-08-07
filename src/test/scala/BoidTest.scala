import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import pcd.ass03.{Boid, P2d, V2d}

class BoidTest extends AnyFlatSpec with should.Matchers:
  
  "Boid" should "set pos" in:
    val boid: Boid = Boid(P2d(0, 0), V2d(0, 0))
    boid.pos shouldEqual P2d(0, 0)
    boid.pos = P2d(1, 1)
    boid.pos shouldEqual P2d(1, 1)

  it should "set vel" in:
    val boid: Boid = Boid(P2d(0, 0), V2d(0, 0))
    boid.vel shouldEqual V2d(0, 0)
    boid.vel = V2d(1, 1)
    boid.vel shouldEqual V2d(1, 1)