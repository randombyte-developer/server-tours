package de.randombyte.servertours

import com.google.inject.Inject
import de.randombyte.servertours.commands.BaseCommand
import de.randombyte.servertours.commands.CreateTourCommand
import de.randombyte.servertours.commands.ListTourWaypointsCommand
import de.randombyte.servertours.config.ConfigManager
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.text.Text

@Plugin(id = ServerTours.ID, name = ServerTours.NAME, version = ServerTours.VERSION, authors = arrayOf(ServerTours.AUTHOR))
class ServerTours @Inject constructor(val logger: Logger,
                                      @DefaultConfig(sharedRoot = true) val configLoader: ConfigurationLoader<CommentedConfigurationNode>) {

    companion object {
        const val NAME = "de.randombyte.servertours.ServerTours"
        const val ID = "de.randombyte.servertours"
        const val VERSION = "v0.1"
        const val AUTHOR = "RandomByte"

        const val PERMISSION = "de.randombyte.servertours"
    }

    @Listener
    fun onInit(event: GameInitializationEvent) {
        ConfigManager.configLoader = configLoader

        fun String.toArgument() = GenericArguments.string(Text.of(this))

        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .executor(BaseCommand())
                //todo child commands: delete tour, delete waypoint, create waypoint, teleport waypoint
                .child(CommandSpec.builder()
                        .arguments("tourName".toArgument())
                        .executor(CreateTourCommand())
                        .build(), "create", "add", "new")
                .child(CommandSpec.builder()
                        .arguments("tourUUID".toArgument())
                        .executor(ListTourWaypointsCommand())
                        .build(), "list", "edit")
                .build(), "serverTours")

        logger.info("$NAME loaded: $VERSION")
    }
}