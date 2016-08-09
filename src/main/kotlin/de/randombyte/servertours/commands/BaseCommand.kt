package de.randombyte.servertours.commands

import de.randombyte.servertours.Tour
import de.randombyte.servertours.config.ConfigManager
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.format.TextColors
import java.util.*

/**
 * This class is the superclass of all command executors in this plugin. Here are some helper methods.
 */
abstract class BaseCommand : CommandExecutor {
    fun CommandSource.executeCommand(command: String) = Sponge.getCommandManager().process(this, command)
    fun String.toCommandException() = CommandException(Text.of(this))

    fun CommandContext.getTour() = ConfigManager.getTour(getOne<String>("tourUUID").asUUID()).orElseThrow {
        "Haven't found any Tour with given tourUUID!".toCommandException()
    }

    fun CommandContext.getWaypointIndex() = getOne<Int>("waypointIndex").checkIfWaypointExists(getTour())

    fun Optional<String>.asUUID() = try {
        UUID.fromString(this.orElseThrow { "tourUUID is missing!".toCommandException() })!!
    } catch (ex: IllegalArgumentException) {
        throw "Invalid UUID!".toCommandException()
    }

    fun Optional<Int>.checkIfWaypointExists(tour: Tour): Int {
        val id = this.orElseThrow { "waypointIndex is missing!".toCommandException() }
        if (!tour.waypoints.indices.contains(id)) throw "Haven't found specified Waypoint in Tour!".toCommandException()
        return id
    }

    fun getDeactivatableText(text: Text, activated: Boolean, clickAction: ClickAction<*>): Text {
        val builder = text.toBuilder()
        return when (activated) {
            true -> builder.color(TextColors.YELLOW).onClick(clickAction)
            false -> builder.color(TextColors.GRAY)
        }.build()
    }
}