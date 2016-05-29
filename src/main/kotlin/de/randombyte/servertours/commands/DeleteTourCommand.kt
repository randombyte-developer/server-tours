package de.randombyte.servertours.commands

import de.randombyte.servertours.ServerTours
import de.randombyte.servertours.config.ConfigManager
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player

class DeleteTourCommand : PermissionNeededCommandExecutor(ServerTours.PERMISSION) {
    override fun executedWithPermission(player: Player, args: CommandContext): CommandResult {
        val uuid = CommandUtils.getUUIDFromArg(args.getOne<String>("tourUUID"))
        ConfigManager.setTours(ConfigManager.getTours().filterNot { it.uuid.equals(uuid) })
        Sponge.getCommandManager().process(player, "serverTours")
        return CommandResult.success()
    }
}