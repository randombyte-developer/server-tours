package de.randombyte.servertours

import com.flowpowered.math.vector.Vector3d
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.title.Title
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

/**
 * A point at a [location] where a player is teleported with a set [headRotation]. An [infoText] appears to the player.
 * [infoTextPlacement] sets where the text appears; possible values are 'chat' and 'title'.
 */
data class Waypoint(val location: Location<World>, val headRotation: Vector3d, val infoText: Text, val infoTextPlacement: String = "chat") {
    fun sendInfoText(player: Player) {
        if (infoTextPlacement == "title") {
            player.sendTitle(Title.builder().stay(600).subtitle(infoText).build())
        } else {
            player.sendMessage(Text.builder("Info-text: ").append(infoText).build())
        }
    }
}