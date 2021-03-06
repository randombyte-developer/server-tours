package de.randombyte.servertours

import org.spongepowered.api.text.Text
import java.util.*

/**
 * Several [Waypoint]s where the player can be teleported to.
 */
data class Tour(val uuid: UUID, val name: Text, val waypoints: List<Waypoint> = emptyList(), val completionCommand: String = "")