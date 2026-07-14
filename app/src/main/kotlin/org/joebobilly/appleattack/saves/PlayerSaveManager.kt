package org.joebobilly.appleattack.saves

import net.kyori.adventure.nbt.BinaryTagIO
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagHandler
import org.joebobilly.appleattack.items.ItemStackSerializer
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.logging.Logger
import kotlin.io.path.Path

object PlayerSaveManager {
    private val logger = Logger.getLogger("player-save-manager")

    private val saveFolder = Path("saves")

    private val inventoryTag: Tag<List<ItemStack>> = ItemStackSerializer.tag("inventory").list().defaultValue(emptyList())

    fun savePlayer(player: Player) {
        val data = serializePlayer(player)
        val playerSaveFolder = saveFolder.resolve("${player.uuid}.nbt").toAbsolutePath()

        try {
            Files.createDirectories(saveFolder.toAbsolutePath())
            Files.newOutputStream(playerSaveFolder, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use {
                BinaryTagIO.writer().write(data, it, BinaryTagIO.Compression.GZIP)
            }
        }
        catch (e: Exception) {
            logger.severe("Failed to save player ${player.username} (${player.uuid}):\n${e.message}")
        }
    }

    fun loadPlayer(player: Player): Boolean {
        val playerSaveFolder = saveFolder.resolve("${player.uuid}.nbt").toAbsolutePath()

        if(Files.exists(playerSaveFolder)) {
            try {
                Files.newInputStream(playerSaveFolder).use {
                    deserializePlayer(player,
                        BinaryTagIO.unlimitedReader().read(playerSaveFolder, BinaryTagIO.Compression.GZIP))
                }
            }
            catch(e: Exception) {
                logger.severe("Failed to load player ${player.username} (${player.uuid}):\n${e.message}")
                return false
            }
        }
        else {
            newPlayer(player)
        }
        return true
    }

    private fun serializePlayer(player: Player): CompoundBinaryTag {
        val handler = TagHandler.newHandler()

        handler.setTag(inventoryTag, listOf(*player.inventory.itemStacks))

        return handler.asCompound()
    }

    private fun deserializePlayer(player: Player, data: CompoundBinaryTag) {
        val handler = TagHandler.fromCompound(data)

        val inventory = handler.getTag(inventoryTag)
        for(i in 0..<inventory.size) {
            player.inventory.setItemStack(i, inventory[i])
        }
    }

    private fun newPlayer(player: Player) {

    }
}