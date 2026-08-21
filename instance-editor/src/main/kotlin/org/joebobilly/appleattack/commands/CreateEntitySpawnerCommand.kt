package org.joebobilly.appleattack.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.joebobilly.appleattack.InstanceEditor
import org.joebobilly.appleattack.entities.EntityTypeClass
import org.joebobilly.appleattack.entities.spawners.SpawnerManager
import org.joebobilly.appleattack.entities.spawners.EntitySpawnerData
import org.joebobilly.appleattack.serialization.PaperSerializationEntry.Companion.persistentDataEntry
import org.joebobilly.appleattack.utils.CommandUtils.filterByCommandInput
import org.joebobilly.appleattack.utils.EnumUtils
import org.joebobilly.appleattack.utils.Position
import org.joebobilly.appleattack.utils.PositionList
import org.joebobilly.appleattack.utils.PositionUtils.fromPaperLocation
import org.joebobilly.appleattack.utils.Sounds
import java.util.EnumSet

object CreateEntitySpawnerCommand : TabExecutor {
    enum class Flag(val char: Char) {
        BYPASS_TYPE_CHECK('b'),
        USE_PLAYER_POSITION('p'),
        USE_POSITION_LIST('l'),
        PROVIDE_MAX_SPAWNED('m')
    }
    val flags = CommandFlags.of(Flag::char)

    val typeLabel = mapOf(
        "mob" to EntityTypeClass.MOB,
        "npc" to EntityTypeClass.NPC
    )

    val skipTypeAlias = mapOf(
        "createnpcspawner" to EntityTypeClass.NPC,
        "createnpc" to EntityTypeClass.NPC,
        "placenpc" to EntityTypeClass.NPC,
        "npcadd" to EntityTypeClass.NPC,
        "createmobspawner" to EntityTypeClass.MOB,
        "createmob" to EntityTypeClass.MOB,
        "placemob" to EntityTypeClass.MOB,
        "mobadd" to EntityTypeClass.MOB
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player) {
            sender.sendMessage(Component.text("Only players can use this command!", NamedTextColor.RED))
            return true
        }
        if(!sender.isInWorld) {
            sender.sendMessage(Component.text("You need to be in a world to use this command!", NamedTextColor.RED))
            return true
        }
        val queue = CommandArgQueue(args)

        val entityClass = skipTypeAlias[label] ?: typeLabel[queue.readString()]
        if(entityClass == null) {
            return false
        }

        val entityTypeId = queue.readString() ?: return false

        val setFlags = queue.readFlags(flags) ?: EnumUtils.emptySet()
        val maxSpawned = if(setFlags.contains(Flag.PROVIDE_MAX_SPAWNED) || entityClass == EntityTypeClass.MOB) {
            try {
                queue.readInt() ?: return false
            }
            catch(_: Exception) {
                sender.sendMessage(Component.text("Invalid max spawned: ${queue.previous()}!", NamedTextColor.RED))
                return true
            }
        } else 1

        if(maxSpawned <= 0) {
            sender.sendMessage(Component.text("Max Spawned must be positive: ${maxSpawned}!", NamedTextColor.RED))
            return true
        }

        if(!setFlags.contains(Flag.BYPASS_TYPE_CHECK)) {
            val entityType = InstanceEditor.infoDump.entityTypes.filter { it.id == entityTypeId.lowercase() }.getOrNull(0)
            if(entityType == null || entityType.entityClass != entityClass) {
                setFlags.add(Flag.BYPASS_TYPE_CHECK)
                sender.sendMessage(Component.text("Unknown ${entityClass.name} type: $entityTypeId!", NamedTextColor.RED))
                sender.sendMessage(
                    Component.text("[Click here to place anyway...]", NamedTextColor.YELLOW)
                        .clickEvent(constructCommand(entityClass, entityTypeId, setFlags, maxSpawned))
                )
                return true
            }
        }

        val usePlayerPosition: Boolean
        val usePositionList: Boolean
        if(setFlags.contains(Flag.USE_PLAYER_POSITION)) {
            usePlayerPosition = true
            usePositionList = setFlags.contains(Flag.USE_POSITION_LIST)
        }
        else if(setFlags.contains(Flag.USE_POSITION_LIST)) {
            usePlayerPosition = false
            usePositionList = true
        }
        else {
            usePlayerPosition = entityClass == EntityTypeClass.NPC
            usePositionList = entityClass == EntityTypeClass.MOB
        }

        if(usePlayerPosition && usePositionList) {
            sender.sendMessage(Component.text("Contradictory flags found!", NamedTextColor.RED))
            setFlags.remove(Flag.USE_POSITION_LIST)
            setFlags.add(Flag.USE_PLAYER_POSITION)
            sender.sendMessage(
                Component.text("[Click here to use your current position]", NamedTextColor.YELLOW)
                    .clickEvent(constructCommand(entityClass, entityTypeId, setFlags, maxSpawned))
            )
            setFlags.remove(Flag.USE_PLAYER_POSITION)
            setFlags.add(Flag.USE_POSITION_LIST)
            sender.sendMessage(
                Component.text("[Click here to use your held position list]", NamedTextColor.YELLOW)
                    .clickEvent(constructCommand(entityClass, entityTypeId, setFlags, maxSpawned))
            )
            return true
        }

        val world: World
        val positions: List<Position>

        if(usePlayerPosition) {
            world = sender.world
            positions = listOf(Position.fromPaperLocation(sender.location))
        }
        else {
            val mainHand = sender.inventory.itemInMainHand
            val positionList = PositionList.POSITION_LIST_ENTRY.persistentDataEntry.get(mainHand.persistentDataContainer)
            val worldNullable = positionList?.getWorld()
            if(positionList == null || worldNullable == null) {
                if(positionList == null) {
                    sender.sendMessage(Component.text("Cannot use your held item as a position list!", NamedTextColor.RED))
                }
                else {
                    sender.sendMessage(Component.text("Your held position list is empty!", NamedTextColor.RED))
                }
                sender.sendMessage(
                    Component.text("[Click here to retry]", NamedTextColor.YELLOW)
                        .clickEvent(constructCommand(entityClass, entityTypeId, setFlags, maxSpawned))
                )
                setFlags.remove(Flag.USE_POSITION_LIST)
                setFlags.add(Flag.USE_PLAYER_POSITION)
                sender.sendMessage(
                    Component.text("[Click here to use your current position]", NamedTextColor.YELLOW)
                        .clickEvent(constructCommand(entityClass, entityTypeId, setFlags, maxSpawned))
                )
                return true
            }
            world = worldNullable
            positions = positionList.getPositions()
        }

        SpawnerManager.addToWorld(world, EntitySpawnerData(
            entityTypeId.lowercase(), positions, maxSpawned
        ))
        sender.playSound(Sounds.POSITION_ADD)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String>? {
        val skipEntityClass = if(skipTypeAlias.contains(label)) 0 else 1

        if(skipEntityClass == 1 && args.size == 1) {
            return typeLabel.keys.toList().filterByCommandInput(args[0])
        }

        val entityClass = skipTypeAlias[label] ?: typeLabel[args[0]]
        if(args.size == skipEntityClass + 1) {
            return InstanceEditor.infoDump.entityTypes.filter { it.entityClass == entityClass }
                .map { it.id }.filterByCommandInput(args[skipEntityClass], String::lowercase)
        }
        if(args.size == skipEntityClass + 2) {
            return flags.suggest(args[skipEntityClass + 1])
        }
        return null
    }

    private fun constructCommand(entityTypeClass: EntityTypeClass, entityTypeId: String, flags: EnumSet<Flag>, maxSpawned: Int): ClickEvent<*> {
        val flags = EnumUtils.copyOf(flags)
        flags.add(Flag.PROVIDE_MAX_SPAWNED)
        val entityClassArg = typeLabel.entries.first { it.value == entityTypeClass }.key
        return ClickEvent.runCommand(
            "createentityspawner $entityClassArg $entityTypeId " + this.flags.getFlagArg(flags) + " $maxSpawned"
        )
    }
}