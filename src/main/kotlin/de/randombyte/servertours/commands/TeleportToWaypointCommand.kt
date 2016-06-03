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
        player.location = tour.waypoints[waypointIndex].location
        player.sendMessage(Text.of("Info-text: ", tour.waypoints[waypointIndex].infoText))
        player.sendMessage(getPreviousWaypointButton(tour, waypointIndex))
        player.sendMessage(getNextWayppointButton(tour, waypointIndex))
        return CommandResult.success()
    }

    private fun getPreviousWaypointButton(tour: Tour, currentWaypointIndex: Int) =
            getDeactivatableText(Text.of("[PREVIOUS WAYPOINT]"), tour.waypoints.indices.contains(currentWaypointIndex - 1),
                    TextActions.runCommand("/serverTours teleport ${tour.uuid} ${currentWaypointIndex - 1}"))

    private fun getNextWayppointButton(tour: Tour, currentWaypointIndex: Int) =
            getDeactivatableText(Text.of(" [NEXT WAYPOINT]"), tour.waypoints.indices.contains(currentWaypointIndex + 1),
                    TextActions.runCommand("/serverTours teleport ${tour.uuid} ${currentWaypointIndex + 1}"))
}