package de.randombyte.servertours.commands

import de.randombyte.servertours.config.ConfigManager
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player

class DeleteTourCommand : PlayerCommandExecutor(){
    override fun executedByPlayer(player: Player, args: CommandContext): CommandResult {
        ConfigManager.deleteTour(args.getOne<String>("tourUUID").asUUID())
        player.executeCommand("serverTours") //Show list after deletion
        return CommandResult.success()
    }
}