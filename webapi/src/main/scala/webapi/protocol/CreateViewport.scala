package webapi.protocol

case class CreateViewport(
    name: String,
    x: Double,
    y: Double,
    z: Double,
    up: CreateViewportVector,
    lookAt: CreateViewportVector,
    hfov: Double,
    near: Double,
    far: Double
)
