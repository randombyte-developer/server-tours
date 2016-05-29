package de.randombyte.servertours.commands

import de.randombyte.servertours.config.ConfigManager
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers
import java.util.*

abstract class BaseCommand : CommandExecutor {
    fun CommandSource.executeCommand(command: String) = Sponge.getCommandManager().process(this, command)
    fun String.toCommandException() = CommandException(Text.of(this))

    fun CommandContext.getTour() = getOne<String>("tourUUID").asUUID().getTour()
    fun CommandContext.getText() = TextSerializers.FORMATTING_CODE.deserialize(getOne<String>("text").orElseThrow {
        "text is missing!".toCommandException()
    })

    fun Optional<String>.asUUID() = try {
        UUID.fromString(this.orElseThrow { "tourUUID is missing!".toCommandException() })
    } catch (illegalAraException: IllegalArgumentException) {
        throw "Invalid UUID!".toCommandException()
    }

    fun UUID.getTour() = ConfigManager.getTour(this).orElseThrow {
        "Haven't found any Tour with given tourUUID!".toCommandException()
    }
}