package de.randombyte.servertours

import com.flowpowered.math.vector.Vector3d
import com.google.inject.Inject
import de.randombyte.servertours.commands.*
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
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*

@Plugin(id = ServerTours.ID, name = ServerTours.NAME, version = ServerTours.VERSION, authors = arrayOf(ServerTours.AUTHOR))
class ServerTours @Inject constructor(val logger: Logger,
                                      @DefaultConfig(sharedRoot = true) val configLoader: ConfigurationLoader<CommentedConfigurationNode>) {

    companion object {
        const val NAME = "ServerTours"
        const val ID = "servertours"
        const val VERSION = "v1.2.1"
        const val AUTHOR = "RandomByte"

        const val EDITING_PERMISSION = "servertours.edit"
        const val VIEW_PERMISSION = "servertours.view"

        val playerStartLocations = mutableMapOf<UUID, Pair<Location<World>, Vector3d>>()
        var frozenPlayers = mapOf<UUID, Location<World>>()
    }

    @Listener
    fun onInit(event: GameInitializationEvent) {
        ConfigManager.configLoader = configLoader

        fun String.toStringArg() = GenericArguments.string(Text.of(this))
        fun String.toIntArg() = GenericArguments.integer(Text.of(this))

        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .executor(ListToursCommand())
                .description(Text.of("This is the only important command because all other commands are called by the text GUI."))
                //Tours
                .child(CommandSpec.builder()
                        .permission(EDITING_PERMISSION)
                        .executor(CreateTourCommand())
                        .build(), "create", "add", "new")
                .child(CommandSpec.builder()
                        .permission(EDITING_PERMISSION)
                        .arguments("tourUUID".toStringArg())
                        .executor(ListTourWaypointsCommand())
                        .build(), "list", "edit")
                .child(CommandSpec.builder()
                        .permission(EDITING_PERMISSION)
                        .arguments("tourUUID".toStringArg())
                        .executor(DeleteTourCommand())
                        .build(), "delete", "remove")
                .child(CommandSpec.builder()
                        .arguments("tourUUID".toStringArg())
                        .executor(StartTourCommand())
                        .build(), "start")
                //Waypoints
                .child(CommandSpec.builder()
                        .permission(EDITING_PERMISSION)
                        .arguments("tourUUID".toStringArg())
                        .executor(CreateWaypointCommand())
                        .build(), "newWaypoint")
                .child(CommandSpec.builder()
                        .permission(EDITING_PERMISSION)
                        .arguments(GenericArguments.seq("tourUUID".toStringArg(), "waypointIndex".toIntArg()))
                        .executor(DeleteWaypointCommand())
                        .build(), "deleteWaypoint")
                .child(CommandSpec.builder()
                        .arguments(GenericArguments.seq("tourUUID".toStringArg(), "waypointIndex".toIntArg()))
                        .executor(TeleportToWaypointCommand())
                        .build(), "teleport")
                .build(), "serverTours")

        Task.builder()
                .intervalTicks(1)
                .execute { ->
                    frozenPlayers = frozenPlayers.filter { Sponge.getServer().getPlayer(it.key).isPresent }
                    frozenPlayers.forEach { Sponge.getServer().getPlayer(it.key).get().location = it.value }
                }.submit(this)

        logger.info("$NAME loaded: $VERSION")
    }
}