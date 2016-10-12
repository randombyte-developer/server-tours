package de.randombyte.servertours.config

import com.flowpowered.math.vector.Vector3i
import com.google.common.reflect.TypeToken
import de.randombyte.servertours.LocationAndRotation
import de.randombyte.servertours.Tour
import de.randombyte.servertours.Waypoint
import ninja.leaping.configurate.ConfigurationNode
import org.spongepowered.api.Sponge
import org.spongepowered.api.text.serializer.TextSerializers
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*

object Deserialization {
    fun deserialize(node: ConfigurationNode): Tour {
        val endPointNode = node.getNode(ConfigManager.END_POINT_NODE)
        val endPoint = if (endPointNode.value == null) null else deserializeLocationAndRotation(endPointNode)

        return Tour(UUID.fromString(node.key.toString()),
                deserializeText(node.getNode(ConfigManager.NAME_NODE).string),
                node.getNode(ConfigManager.WAYPOINTS_NODE).childrenList.map { deserializeWaypoint(it) },
                node.getNode(ConfigManager.COMPLETION_COMMAND_NODE).string,
                endPoint)
    }

    private fun deserializeWaypoint(node: ConfigurationNode): Waypoint =
            Waypoint(deserializeLocation(node.getNode(ConfigManager.LOCATION_NODE)),
                    deserializeVector3i(node.getNode(ConfigManager.HEAD_ROTATION_NODE)).toDouble(),
                    deserializeText(node.getNode(ConfigManager.INFO_TEXT_NODE).string),
                    node.getNode(ConfigManager.INFO_TEXT_PLACEMENT_NODE).string)

    private fun deserializeLocationAndRotation(node: ConfigurationNode) =
            LocationAndRotation(deserializeLocation(node.getNode(ConfigManager.LOCATION_NODE)),
                    deserializeVector3i(node.getNode(ConfigManager.HEAD_ROTATION_NODE)).toDouble())

    private fun deserializeLocation(node: ConfigurationNode): Location<World> {
        val worldUUID = node.getNode(ConfigManager.WORLD_UUID_NODE).string
        return Location(Sponge.getServer().getWorld(UUID.fromString(worldUUID))
                .orElseThrow { Exception("No world with uuid '$worldUUID' loaded!") },
                deserializeVector3i(node.getNode(ConfigManager.POSITION_NODE)))
    }

    private fun deserializeNullableLocation(node: ConfigurationNode) : Location<World>? =
            if (node.value == null) null else deserializeLocation(node)

    private fun deserializeVector3i(node: ConfigurationNode): Vector3i {
        val coordinatesList = node.getValue(object : TypeToken<List<Int>>() {})
        return Vector3i(coordinatesList[0], coordinatesList[1], coordinatesList[2])
    }

    private fun deserializeText(text: String) = TextSerializers.FORMATTING_CODE.deserialize(text)
}