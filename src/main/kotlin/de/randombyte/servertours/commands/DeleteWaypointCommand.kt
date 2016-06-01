package de.randombyte.servertours.commands

import de.randombyte.servertours.config.ConfigManager
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player

class DeleteWaypointCommand : PlayerCommandExecutor() {
    override fun executedByPlayer(player: Player, args: CommandContext): CommandResult {
        val tour = args.getTour()
        ConfigManager.deleteWaypoint(tour, args.getWaypointIndex())
        player.executeCommand("serverTours list ${tour.uuid}")
        return CommandResult.success()
    }
}