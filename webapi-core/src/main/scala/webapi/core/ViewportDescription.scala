package webapi.core

case class ViewportDescription(
    name: String,
    x: Double,
    y: Double,
    z: Double,
    up: ViewportVectorDescription,
    lookAt: ViewportVectorDescription,
    hfov: Double,
    near: Double,
    far: Double
)
