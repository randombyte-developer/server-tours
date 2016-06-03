package de.randombyte.servertours.commands

import de.randombyte.servertours.ServerTours
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors

class StartTourCommand : PlayerCommandExecutor() {
    override fun executedByPlayer(player: Player, args: CommandContext): CommandResult {
        val tour = args.getTour()
        if (tour.waypoints.size == 0) throw "Specified Tour doesn't have any Waypoints!".toCommandException()
        ServerTours.playerStartLocations[player.uniqueId] = player.location
        player.sendMessage(Text.of(TextColors.GRAY, "Starting Tour '", tour.name, "'..."))
        player.executeCommand("serverTours teleport ${tour.uuid} 0")
        return CommandResult.success()
    }
}