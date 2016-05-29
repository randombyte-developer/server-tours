package de.randombyte.servertours.commands

import de.randombyte.servertours.ServerTours
import de.randombyte.servertours.Tour
import de.randombyte.servertours.config.ConfigManager
import de.randombyte.servertours.commands.CommandUtils.toCommandException
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.service.pagination.PaginationService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextAction
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors
import java.util.*

class ListTourWaypointsCommand : PermissionNeededCommandExecutor(ServerTours.PERMISSION) {
    override fun executedWithPermission(player: Player, args: CommandContext): CommandResult {
        val uuid = CommandUtils.getUUIDFromArg(args.getOne<String>("tourUUID"))
        sendWaypointsList(player, ConfigManager.getTourByUUID(uuid).orElseThrow {
            "Haven't found any Tour with given tourUUID!".toCommandException()
        })
        return CommandResult.success()
    }

    private fun sendWaypointsList(player: Player, tour: Tour) {
        Sponge.getServiceManager().provide(PaginationService::class.java).ifPresent {
            it.builder()
                    .header(getHeader(tour))
                    .contents(getWaypointsTexts(tour))
                    .sendTo(player)
        }
    }

    private fun getHeader(tour: Tour) = Text.builder()
            .append(BaseCommand.SPACER)
            .append(Text.of(TextColors.YELLOW, "${tour.waypoints.size} Waypoint(s) | "))
            .append(getCreateWaypointButton(tour.uuid))
            .append(BaseCommand.SPACER)
            .build()

    private fun getCreateWaypointButton(tourUUID: UUID) = Text.builder(" [NEW WAYPOINT]").color(TextColors.RED)
            .onClick(TextActions.runCommand("/serverTours newWaypoint $tourUUID")).build()

    private fun getWaypointsTexts(tour: Tour) = tour.waypoints.mapIndexed { i, waypoint ->
        Text.builder()
                .append(getDeactivatableText("[/\\]", i > 0, TextActions.executeCallback {
                    ConfigManager.moveWaypointUp(tour, i)
                }))
                .append(getDeactivatableText(" [\\/]", i == tour.waypoints.lastIndex, TextActions.executeCallback {
                    ConfigManager.moveWaypointDown(tour, i)
                }))
                .append(Text.of("#$i"))
                .append(Text.builder(" [TELEPORT]").color(TextColors.YELLOW)
                        .onClick(TextActions.runCommand("/serverTours teleport ${tour.uuid} $i")).build())
                .append(Text.builder(" [DELETE]").color(TextColors.RED).
                        onClick(TextActions.runCommand("/serverTours delete ${tour.uuid} $i")).build())
        .build()
    }

    private fun <R> getDeactivatableText(text: String, activated: Boolean, textAction: TextAction<R>) =
            Text.of(if (activated) TextColors.YELLOW else TextColors.GRAY, text, if (activated) textAction else null)
}