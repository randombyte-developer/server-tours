package de.randombyte.servertours.commands

import de.randombyte.servertours.ServerTours
import de.randombyte.servertours.Tour
import de.randombyte.servertours.config.ConfigManager
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.service.pagination.PaginationService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*

class ListTourWaypointsCommand : PermissionNeededCommandExecutor(ServerTours.PERMISSION) {
    override fun executedWithPermission(player: Player, args: CommandContext): CommandResult {
        sendWaypointsList(player, args.getTour())
        return CommandResult.success()
    }

    private fun sendWaypointsList(player: Player, tour: Tour) {
        Sponge.getServiceManager().provide(PaginationService::class.java).ifPresent {
            it.builder()
                    .header(getHeader(tour))
                    .contents(getWaypointsTexts(player, tour))
                    .sendTo(player)
        }
    }

    private fun getHeader(tour: Tour) = Text.builder()
            .append(ListToursCommand.getSpacer(3))
            .append(Text.builder("[BACK]").color(TextColors.YELLOW).onClick(TextActions.runCommand("/serverTours")).build())
            .append(ListToursCommand.getSpacer(3))
            .append(Text.of(TextColors.YELLOW, "${tour.waypoints.size} Waypoint(s) | "))
            .append(getCreateWaypointButton(tour.uuid))
            .append(ListToursCommand.getSpacer(10))
            .build()

    private fun getCreateWaypointButton(tourUUID: UUID) = Text.builder(" [NEW WAYPOINT]").color(TextColors.RED)
            .onClick(TextActions.runCommand("/serverTours newWaypoint $tourUUID")).build()

    private fun getWaypointsTexts(player: Player, tour: Tour) = tour.waypoints.mapIndexed { i, waypoint ->
        Text.builder()
                .append(getDeactivatableText("▲", i != 0, TextActions.executeCallback {
                    ConfigManager.moveWaypointUp(tour, i)
                    player.executeCommand("serverTours list ${tour.uuid}") //Show waypoint list after reordering
                }))
                .append(getDeactivatableText("▼", i != tour.waypoints.lastIndex, TextActions.executeCallback {
                    ConfigManager.moveWaypointDown(tour, i)
                    player.executeCommand("serverTours list ${tour.uuid}")
                }))
                .append(Text.of("#$i"))
                .append(Text.builder(" [TELEPORT]").color(TextColors.YELLOW)
                        .onClick(TextActions.runCommand("/serverTours teleport ${tour.uuid} $i")).build())
                .append(Text.builder(" [DELETE]").color(TextColors.RED).
                        onClick(TextActions.runCommand("/serverTours delete ${tour.uuid} $i")).build())
        .build()
    }

    private fun getDeactivatableText(text: String, activated: Boolean, clickAction: ClickAction<*>): Text {
        val builder = Text.builder(text)
        if (activated) builder.onClick(clickAction)
        return builder.color(TextColors.YELLOW).style(TextStyles.BOLD).build()
    }
}