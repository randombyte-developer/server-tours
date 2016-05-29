package de.randombyte.servertours.commands

import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.text.Text
import java.util.*

abstract class BaseCommand : CommandExecutor {
    fun CommandSource.executeCommand(command: String) = Sponge.getCommandManager().process(this, command)
    fun String.toCommandException() = CommandException(Text.of(this))

    fun Optional<String>.asUUID() = try {
        UUID.fromString(this.orElseThrow { "tourUUID is missing!".toCommandException() })
    } catch (illegalAraException: IllegalArgumentException) {
        throw "Invalid UUID!".toCommandException()
    }
}