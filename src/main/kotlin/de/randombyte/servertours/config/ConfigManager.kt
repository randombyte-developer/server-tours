package de.randombyte.servertours.config

import de.randombyte.servertours.Tour
import de.randombyte.servertours.Waypoint
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
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

    fun getTours(): Map<UUID, Tour> = configLoader.load().getNode(TOURS_NODE).childrenMap.map { tourNode ->
        val tour = Deserialization.deserialize(tourNode.value)
        tour.uuid to tour
    }.toMap()

    fun setTours(tours: Map<UUID, Tour>) {
        val rootNode = configLoader.load()
        val toursNode = rootNode.getNode(TOURS_NODE)
        toursNode.value = null //Clear
        tours.forEach { Serialization.serialize(it.value, toursNode.getNode(it.key.toString())) }
        configLoader.save(rootNode)
    }

    fun addTour(tour: Tour) = setTours(getTours() + (tour.uuid to tour))
    fun deleteTour(uuid: UUID) = setTours(getTours().filterNot { it.key.equals(uuid) })
    fun getTour(uuid: UUID) = Optional.ofNullable<Tour>(getTours()[uuid])

    fun addWaypoint(tour: Tour, waypoint: Waypoint) =
        ConfigManager.setTours(ConfigManager.getTours() + (tour.uuid to tour.copy(waypoints = tour.waypoints + waypoint)))
    fun deleteWaypoint(tour: Tour, index: Int) =
            ConfigManager.setTours(ConfigManager.getTours() +
                    (tour.uuid to tour.copy(waypoints = tour.waypoints.filterIndexed { i, waypoint -> i != index })))

    enum class Direction {UP, DOWN}
    fun moveWaypoint(tour: Tour, waypointIndex: Int, direction: Direction) {
        val newTour = when (direction) {
            Direction.UP -> tour.copy(waypoints = tour.waypoints.swap(waypointIndex - 1, waypointIndex))
            Direction.DOWN -> tour.copy(waypoints = tour.waypoints.swap(waypointIndex, waypointIndex + 1))
        }
        setTours(getTours() + (tour.uuid to newTour))
    }

    fun <T> List<T>.swap(first: Int, second: Int) = take(first) + get(second) + get(first) + drop(second + 1)
}