package de.randombyte.servertours.commands

import de.randombyte.servertours.ServerTours
import de.randombyte.servertours.Tour
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions

class TeleportToWaypointCommand : PlayerCommandExecutor() {
    override fun executedByPlayer(player: Player, args: CommandContext): CommandResult {
        val tour = args.getTour()
        if (!player.hasPermission("${ServerTours.VIEW_PERMISSION}.${tour.uuid}"))
            throw "You don't have the permission to teleport to that Waypoint!".toCommandException()
        val waypointIndex = args.getWaypointIndex()
        val waypoint = tour.waypoints[waypointIndex]
        player.setLocationAndRotationSafely(waypoint.location, waypoint.headRotation)
        if (waypoint.freezePlayer) {
            ServerTours.frozenPlayers += player.uniqueId to waypoint.location
        }
        tour.waypoints[waypointIndex].sendInfoText(player)
        player.sendMessage(getNavigationButtons(tour, waypointIndex, player))
        return CommandResult.success()
    }

    private fun getNavigationButtons(tour: Tour, waypointIndex: Int, player: Player) = Text.builder()
            .append(getPreviousWaypointButton(tour, waypointIndex))
            .append(getNextWaypointButton(tour, waypointIndex, player)).build()

    private fun getPreviousWaypointButton(tour: Tour, currentWaypointIndex: Int) =
            getDeactivatableText(Text.of("[PREVIOUS WAYPOINT]"), tour.waypoints.indices.contains(currentWaypointIndex - 1),
                    TextActions.runCommand("/serverTours teleport ${tour.uuid} ${currentWaypointIndex - 1}"))

    private fun getNextWaypointButton(tour: Tour, currentWaypointIndex: Int, player: Player): Text {
        val nextWaypointExists = tour.waypoints.indices.contains(currentWaypointIndex + 1)
        return if (!nextWaypointExists && ServerTours.playerStartLocations.containsKey(player.uniqueId)) {
            getDeactivatableText(Text.of(" [END TOUR]"), true, TextActions.executeCallback {
                leaveWaypoint(player)
                val homeLocationAndRotation = ServerTours.playerStartLocations.remove(player.uniqueId)
                if (homeLocationAndRotation != null) {
                    player.setLocationAndRotation(homeLocationAndRotation.first, homeLocationAndRotation.second)
                }
                if (tour.completionCommand.isNotBlank()) {
                    Sponge.getServer().console.executeCommand(tour.completionCommand.replace("\$player", player.name))
                }
            })
        } else {
            getDeactivatableText(Text.of(" [NEXT WAYPOINT]"), nextWaypointExists,
                    TextActions.executeCallback {
                        leaveWaypoint(player)
                        player.executeCommand("serverTours teleport ${tour.uuid} ${currentWaypointIndex + 1}")
                    })
        }
    }

    private fun leaveWaypoint(player: Player) {
        // filterNot to remove entry
        ServerTours.frozenPlayers = ServerTours.frozenPlayers.filterNot { it.key == player.uniqueId }
        player.clearTitle() // The info-texts might be sent as a title; clear it
    }
}