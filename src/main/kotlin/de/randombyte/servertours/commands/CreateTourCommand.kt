package de.randombyte.servertours.commands

import de.randombyte.servertours.ServerTours
import de.randombyte.servertours.Tour
import de.randombyte.servertours.config.ConfigManager
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.serializer.TextSerializers
import java.util.*

class CreateTourCommand : PermissionNeededCommandExecutor(ServerTours.PERMISSION){
    override fun executedWithPermission(player: Player, args: CommandContext): CommandResult {
        val name = args.getOne<String>("tourName").orElseThrow { CommandException(Text.of("tourName is missing!")) }
        ConfigManager.setTours(ConfigManager.getTours() + Tour(UUID.randomUUID(), TextSerializers.FORMATTING_CODE.deserialize(name)))
        player.sendMessage(Text.of(TextColors.GREEN, "Created new Tour!"))
        return CommandResult.success()
    }
}