package de.randombyte.servertours.commands

import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player

class StartTourCommand : PlayerCommandExecutor() {
    override fun executedByPlayer(player: Player, args: CommandContext): CommandResult {
        val tour = args.getTour()
        if (tour.waypoints.size == 0) throw "Specified Tour doesn't have any Waypoints!".toCommandException()
        player.executeCommand("serverTours teleport ${tour.uuid} 0")
        return CommandResult.success()
    }
}