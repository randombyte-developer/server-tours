package de.randombyte.servertours.commands

import de.randombyte.servertours.ServerTours
import de.randombyte.servertours.Tour
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions

class TeleportToWaypointCommand : PlayerCommandExecutor() {
    override fun executedByPlayer(player: Player, args: CommandContext): CommandResult {
        val tour = args.getTour()
        if (!player.hasPermission("${ServerTours.PERMISSION}.${tour.uuid}"))
            throw "You don't have the permission to teleport to that Waypoint!".toCommandException()
        val waypointIndex = args.getWaypointIndex()
        val waypoint = tour.waypoints[waypointIndex]
        player.setLocationAndRotationSafely(waypoint.location, waypoint.headRotation)
        player.sendMessage(Text.of("Info-text: ", tour.waypoints[waypointIndex].infoText))
        player.sendMessage(getNavigationButtons(tour, waypointIndex, player))
        return CommandResult.success()
    }

    private fun getNavigationButtons(tour: Tour, waypointIndex: Int, player: Player) = Text.builder()
            .append(getPreviousWaypointButton(tour, waypointIndex))
            .append(getNextWayppointButton(tour, waypointIndex, player)).build()

    private fun getPreviousWaypointButton(tour: Tour, currentWaypointIndex: Int) =
            getDeactivatableText(Text.of("[PREVIOUS WAYPOINT]"), tour.waypoints.indices.contains(currentWaypointIndex - 1),
                    TextActions.runCommand("/serverTours teleport ${tour.uuid} ${currentWaypointIndex - 1}"))

    private fun getNextWayppointButton(tour: Tour, currentWaypointIndex: Int, player: Player): Text {
        val nextWaypointExists = tour.waypoints.indices.contains(currentWaypointIndex + 1)
        return if (!nextWaypointExists && ServerTours.playerStartLocations.containsKey(player.uniqueId)) {
            getDeactivatableText(Text.of(" [END TOUR]"), true, TextActions.executeCallback {
                val homeLocation = ServerTours.playerStartLocations.remove(player.uniqueId)
                if (homeLocation != null) player.location = homeLocation
            })
        } else {
            getDeactivatableText(Text.of(" [NEXT WAYPOINT]"), nextWaypointExists,
                    TextActions.runCommand("/serverTours teleport ${tour.uuid} ${currentWaypointIndex + 1}"))
        }
    }
}