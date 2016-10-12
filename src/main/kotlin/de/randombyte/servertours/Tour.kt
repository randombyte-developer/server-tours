package de.randombyte.servertours

import org.spongepowered.api.text.Text
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*

/**
 * Several [Waypoint]s where the player can be teleported to.
 */
data class Tour(val uuid: UUID, val name: Text, val waypoints: List<Waypoint> = emptyList(),
                val completionCommand: String = "", val endPoint: LocationAndRotation? = null)