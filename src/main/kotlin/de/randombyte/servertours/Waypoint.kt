package de.randombyte.servertours

import com.flowpowered.math.vector.Vector3d
import org.spongepowered.api.text.Text
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

/**
 * A point at a [location] where a player is teleported with a set [headRotation]. An [infoText] will be sent the player.
 */
data class Waypoint(val location: Location<World>, val headRotation: Vector3d, val infoText: Text)