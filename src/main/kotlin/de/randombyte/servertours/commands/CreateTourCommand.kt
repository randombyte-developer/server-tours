package de.randombyte.servertours.commands

import de.randombyte.servertours.ServerTours
import de.randombyte.servertours.Tour
import de.randombyte.servertours.config.ConfigManager
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import java.util.*

class CreateTourCommand : PermissionNeededCommandExecutor(ServerTours.PERMISSION){
    override fun executedWithPermission(player: Player, args: CommandContext): CommandResult {
        val uuid = UUID.randomUUID()
        ConfigManager.addTour(Tour(uuid, Text.of(uuid.toString().subSequence(0, 7))))
        player.executeCommand("serverTours") //Show tour list
        return CommandResult.success()
    }
}