package de.randombyte.servertours.commands

import de.randombyte.servertours.ServerTours
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player

class TeleportToWaypointCommand : PlayerCommandExecutor() {
    override fun executedByPlayer(player: Player, args: CommandContext): CommandResult {
        val tour = args.getTour()
        if (!player.hasPermission("${ServerTours.PERMISSION}.${tour.uuid}"))
            throw "You don't have the permission to teleport to that Waypoint!".toCommandException()
        val waypointIndex = args.getWaypointIndex()
        player.location = tour.waypoints[waypointIndex].location
        return CommandResult.success()
    }
}