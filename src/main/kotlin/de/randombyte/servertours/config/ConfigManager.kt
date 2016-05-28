package de.randombyte.servertours.config

import com.flowpowered.math.vector.Vector3d
import com.flowpowered.math.vector.Vector3i
import com.google.common.reflect.TypeToken
import de.randombyte.servertours.Tour
import de.randombyte.servertours.Waypoint
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.SimpleConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import org.spongepowered.api.Sponge
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*

object ConfigManager {

    const val TOURS_NODE = "tours"
    const val NAME_NODE = "name"
    const val WAYPOINTS_NODE = "waypoints"
    const val LOCATION_NODE = "location"
    const val WORLD_UUID_NODE = "worldUUID"
    const val POSITION_NODE = "position"
    const val HEAD_ROTATION_NODE = "rotation"
    const val INFO_TEXT_NODE = "infotext"

    //Initialized in init phase of plugin
    lateinit var configLoader: ConfigurationLoader<out ConfigurationNode>

    fun getTours(): List<Tour> = configLoader.load().getNode(TOURS_NODE).childrenMap.map { tourNode ->
        Deserialization.deserialize(tourNode.value)
    }

    fun setTours(tours: List<Tour>) {
        val rootNode = configLoader.load()
        val toursNode = rootNode.getNode(TOURS_NODE)
        toursNode.value = null //Clear
        tours.forEach { Serialization.serialize(it, toursNode.getNode(it.uuid.toString())) }
        configLoader.save(rootNode)
    }

    fun getTourByUUID(uuid: UUID) = Optional.ofNullable<Tour>(getTours().firstOrNull { it.uuid.equals(uuid) })

    //Extracted from ListTourWaypointsCommand
    fun <T> List<T>.swap(first: Int, second: Int) = take(first) + get(second) + get(first) + drop(size - 1 - second)
    fun moveWaypointUp(tour: Tour, waypointIndex: Int) =
            setTours(getTours().filterNot { it.uuid.equals(tour.uuid) } + tour.copy(waypoints = tour.waypoints.swap(waypointIndex, waypointIndex - 1)))
    fun moveWaypointDown(tour: Tour, waypointIndex: Int) =
            setTours(getTours().filterNot { it.uuid.equals(tour.uuid) } + tour.copy(waypoints = tour.waypoints.swap(waypointIndex, waypointIndex + 1)))

    private object Serialization {
        fun serialize(tour: Tour, node: ConfigurationNode) {
            node.getNode(NAME_NODE).value = serializeText(tour.name)
            node.getNode(WAYPOINTS_NODE).value = tour.waypoints.map { serializeWaypoint(it, SimpleConfigurationNode.root()) }
        }

        private fun serializeWaypoint(waypoint: Waypoint, node: ConfigurationNode): ConfigurationNode {
            serializeLocation(waypoint.location, node.getNode(LOCATION_NODE))
            node.getNode(HEAD_ROTATION_NODE).value = serializeVector3d(waypoint.headRotation)
            node.getNode(INFO_TEXT_NODE).value = serializeText(waypoint.infoText)
            return node
        }

        private fun serializeLocation(location: Location<World>, node: ConfigurationNode) {
            node.getNode(WORLD_UUID_NODE).value = location.extent.uniqueId.toString()
            node.getNode(POSITION_NODE).value = serializeVector3i(location.blockPosition)
        }

        private fun serializeVector3i(vector3i: Vector3i) = listOf(vector3i.x, vector3i.y, vector3i.z)
        private fun serializeVector3d(vector3d: Vector3d) = listOf(vector3d.x, vector3d.y, vector3d.z)

        private fun serializeText(text: Text) = TextSerializers.FORMATTING_CODE.serialize(text)
    }

    private object Deserialization {
        fun deserialize(node: ConfigurationNode): Tour =
                Tour(UUID.fromString(node.key.toString()),
                        deserializeText(node.getNode(NAME_NODE).string),
                        node.getNode(WAYPOINTS_NODE).childrenList.map { deserializeWaypoint(it) })

        private fun deserializeWaypoint(node: ConfigurationNode): Waypoint =
                Waypoint(deserializeLocation(node.getNode(LOCATION_NODE)),
                        deserializeVector3d(node.getNode(HEAD_ROTATION_NODE)),
                        deserializeText(node.getNode(INFO_TEXT_NODE).string))

        private fun deserializeLocation(node: ConfigurationNode): Location<World> {
            val worldUUID = node.getNode(WORLD_UUID_NODE).string
            return Location(Sponge.getServer().getWorld(worldUUID)
                    .orElseThrow { Exception("No world with uuid '$worldUUID' loaded!") },
                    deserializeVector3i(node.getNode(POSITION_NODE)))
        }

        private fun deserializeVector3i(node: ConfigurationNode): Vector3i {
            val coordinatesList = node.getList(TypeToken.of(Int::class.java))
            return Vector3i(coordinatesList[0], coordinatesList[1], coordinatesList[2])
        }
        private fun deserializeVector3d(node: ConfigurationNode): Vector3d {
            val coordinatesList = node.getList(TypeToken.of(Double::class.java))
            return Vector3d(coordinatesList[0], coordinatesList[1], coordinatesList[2])
        }

        private fun deserializeText(text: String) = TextSerializers.FORMATTING_CODE.deserialize(text)
    }
}