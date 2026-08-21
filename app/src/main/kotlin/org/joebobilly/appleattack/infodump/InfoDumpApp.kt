package org.joebobilly.appleattack.infodump

import net.kyori.adventure.nbt.BinaryTagIO
import net.minestom.server.tag.TagHandler
import org.joebobilly.appleattack.entities.AAEntityTypeManager
import org.joebobilly.appleattack.init
import org.joebobilly.appleattack.serialization.MinestomSerializationContext
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

fun main() {
    init()

    val dump = InfoDump()
    fillDump(dump)

    val handler = TagHandler.newHandler()
    InfoDump.Serializer.write(MinestomSerializationContext(handler), dump)

    val infoDumpPath = Path.of("instance-editor/src/main/resources")
    Files.createDirectories(infoDumpPath.toAbsolutePath())
    Files.newOutputStream(infoDumpPath.resolve("info_dump.nbt"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use {
        BinaryTagIO.writer().write(handler.asCompound(), it, BinaryTagIO.Compression.GZIP)
    }
}

fun fillDump(dump: InfoDump) {
    dump.entityTypes.addAll(AAEntityTypeManager.list().map {
        EntityDumpEntry(
            it.id, it.entityName(), it.startingEntityType.key().asMinimalString(), it.entityClass
        )
    })
}