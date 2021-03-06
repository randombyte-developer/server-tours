package de.randombyte.servertours.commands

import de.randombyte.servertours.Tour
import de.randombyte.servertours.config.ConfigManager
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.service.pagination.PaginationService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors
import java.util.*

/**
 * This command should be the only one typed by the user. All other commands that need to be executed should be pre-filled
 * by a text-GUI-click.
 */
class ListToursCommand : PlayerCommandExecutor() {
    companion object {
        fun getSpacer(amount: Int) = Text.of(TextColors.WHITE, " ${"=".repeat(amount)} ")
    }

    override fun executedByPlayer(player: Player, args: CommandContext): CommandResult {
        val tours = ConfigManager.getTours().values.toList()
        if (tours.size > 0) {
            Sponge.getServiceManager().provide(PaginationService::class.java).ifPresent {
                it.builder()
                        .header(getHeader(tours.size))
                        .contents(getToursTexts(tours))
                        .sendTo(player)
            }
        } else {
            player.sendMessage(getHeader(0))
        }
        return CommandResult.success()
    }

    private fun getHeader(tourCount: Int) = Text.builder()
            .append(getSpacer(10))
            .append(Text.of(TextColors.YELLOW, "$tourCount saved Tour(s) | "))
            .append(Text.builder("[CREATE TOUR]").color(TextColors.GREEN)
                    .onClick(TextActions.runCommand("/serverTours create")).build())
            .append(getSpacer(10))
            .build()

    private fun getStartTourButton(tourUUID: UUID) = Text.builder(" [START]").color(TextColors.GREEN)
            .onClick(TextActions.runCommand("/serverTours start $tourUUID")).build()

    private fun getEditTourButton(tourUUID: UUID) = Text.builder(" [EDIT]").color(TextColors.YELLOW)
            .onClick(TextActions.runCommand("/serverTours edit $tourUUID")).build()

    private fun getDeleteTourButton(tourUUID: UUID) = Text.builder(" [DELETE]").color(TextColors.RED)
            .onClick(TextActions.suggestCommand("/serverTours delete $tourUUID")).build()

    private fun getToursTexts(tours: List<Tour>): List<Text> = tours.map { tour ->
        Text.builder()
                .append(Text.of("\""))
                .append(tour.name.toBuilder() .onHover(TextActions.showText(Text.of(tour.uuid))).build())
                .append(Text.of("\""))
                .append(getStartTourButton(tour.uuid))
                .append(getEditTourButton(tour.uuid))
                .append(getDeleteTourButton(tour.uuid))
        .build()
    }
}