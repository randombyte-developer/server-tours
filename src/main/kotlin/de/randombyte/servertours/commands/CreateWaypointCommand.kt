package de.randombyte.servertours.commands

import de.randombyte.servertours.ServerTours
import de.randombyte.servertours.Waypoint
import de.randombyte.servertours.config.ConfigManager
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import java.util.*

class CreateWaypointCommand : PermissionNeededCommandExecutor(ServerTours.PERMISSION) {
    override fun executedWithPermission(player: Player, args: CommandContext): CommandResult {
        val tour = args.getTour()
        val randomText = Text.of(UUID.randomUUID().toString().subSequence(0, 7)) //Not that random...
        ConfigManager.addWaypoint(tour, Waypoint(player.location, player.headRotation, randomText))
        player.executeCommand("serverTours list ${tour.uuid}")
        return CommandResult.success()
    }
}