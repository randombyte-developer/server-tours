package de.randombyte.servertours.commands

import org.spongepowered.api.command.CommandException
import org.spongepowered.api.text.Text
import java.util.*

object CommandUtils {

    fun String.toCommandException() = CommandException(Text.of(this))

    fun getUUIDFromArg(optUUIDString: Optional<String>) = try {
        UUID.fromString(optUUIDString.orElseThrow { "tourUUID is missing!".toCommandException() })
    } catch (illegalAraException: IllegalArgumentException) {
        throw "Invalid UUID!".toCommandException()
    }

}