import com.google.inject.Inject
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import org.slf4j.Logger
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.plugin.Plugin

@Plugin(id = ServerTours.ID, name = ServerTours.NAME, version = ServerTours.VERSION, authors = arrayOf(ServerTours.AUTHOR))
class ServerTours @Inject constructor(val logger: Logger,
                                      @DefaultConfig(sharedRoot = true) val configLoader: ConfigurationLoader<CommentedConfigurationNode>) {

    companion object {
        const val NAME = "ServerTours"
        const val ID = "de.randombyte.servertours"
        const val VERSION = "v0.1"
        const val AUTHOR = "RandomByte"
    }

    @Listener
    fun onInit(event: GameInitializationEvent) {
        logger.info("$NAME loaded: $VERSION")
    }
}