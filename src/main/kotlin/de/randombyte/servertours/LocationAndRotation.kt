package de.randombyte.servertours

import com.flowpowered.math.vector.Vector3d
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

data class LocationAndRotation(val location: Location<World>, val headRotation: Vector3d)