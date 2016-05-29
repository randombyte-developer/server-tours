package de.randombyte.servertours.commands

import de.randombyte.servertours.ServerTours
import de.randombyte.servertours.config.ConfigManager
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player

class DeleteTourCommand : PermissionNeededCommandExecutor(ServerTours.PERMISSION) {
    override fun executedWithPermission(player: Player, args: CommandContext): CommandResult {
        ConfigManager.deleteTour(args.getOne<String>("tourUUID").asUUID())
        player.executeCommand("serverTours") //Show list after deletion
        return CommandResult.success()
    }
}