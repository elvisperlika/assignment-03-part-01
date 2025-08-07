package pcd.ass03

class BoidsModel(
    val nBoids: Int,
    val separationWeight: Double,
    val alignmentWeight: Double,
    val cohesionWeight: Double,
    val environmentWidth: Int,
    val environmentHeight: Int,
    val maxSpeed: Double,
    val perceptionRadius: Double,
    val avoidRadius: Double
) {}
