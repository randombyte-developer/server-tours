package de.randombyte.servertours.config

import com.flowpowered.math.vector.Vector3i
import de.randombyte.servertours.Tour
import de.randombyte.servertours.Waypoint
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.SimpleConfigurationNode
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

object Serialization {
    fun serialize(tour: Tour, node: ConfigurationNode) {
        node.getNode(ConfigManager.NAME_NODE).value = serializeText(tour.name)
        node.getNode(ConfigManager.WAYPOINTS_NODE).value = tour.waypoints.map { serializeWaypoint(it, SimpleConfigurationNode.root()) }
        node.getNode(ConfigManager.COMPLETION_COMMAND_NODE).value = tour.completionCommand
    }

    private fun serializeWaypoint(waypoint: Waypoint, node: ConfigurationNode): ConfigurationNode {
        serializeLocation(waypoint.location, node.getNode(ConfigManager.LOCATION_NODE))
        node.getNode(ConfigManager.HEAD_ROTATION_NODE).value = serializeVector3i(waypoint.headRotation.toInt())
        node.getNode(ConfigManager.INFO_TEXT_NODE).value = serializeText(waypoint.infoText)
        return node
    }

    private fun serializeLocation(location: Location<World>, node: ConfigurationNode) {
        node.getNode(ConfigManager.WORLD_UUID_NODE).value = location.extent.uniqueId.toString()
        node.getNode(ConfigManager.POSITION_NODE).value = serializeVector3i(location.blockPosition)
    }

    private fun serializeVector3i(vector3i: Vector3i) = listOf(vector3i.x, vector3i.y, vector3i.z)

    private fun serializeText(text: Text) = TextSerializers.FORMATTING_CODE.serialize(text)
}