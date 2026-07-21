package org.joebobilly.appleattack.utils

import net.kyori.adventure.key.InvalidKeyException
import net.kyori.adventure.key.Key
import net.kyori.adventure.nbt.BinaryTag
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.codec.Codec
import net.minestom.server.codec.Result
import net.minestom.server.codec.Transcoder
import net.minestom.server.coordinate.Pos
import net.minestom.server.item.component.BannerPatterns
import net.minestom.server.network.player.ResolvableProfile
import net.minestom.server.registry.RegistryTranscoder
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagReadable

object TagUtils {
    inline fun <reified R : Enum<R>> enumTag(key: String): Tag<R> {
        return Tag.String(key).map({
            string ->
            try {
                return@map enumValueOf<R>(string.uppercase())
            }
            catch(_: IllegalArgumentException) {
                return@map null
            }
        }, {
            value -> value?.name?.lowercase()
        })
    }

    fun <R> wrapCodecTag(key: String, codec: Codec<R>, transcoderSupplier: () -> Transcoder<BinaryTag>): Tag<R> {
        return Tag.NBT(key).map({
                nbt ->
                val result = codec.decode(transcoderSupplier(), nbt)
                if(result is Result.Error<R>) {
                    throw NBTReadError("", result.message)
                    // println(MinestomAdventure.tagStringIO().asString(nbt))
                    // println(result.message)
                }
                result.orElse(null)
            }, {
                value -> codec.encode(transcoderSupplier(), value).orElse(null)
            }
        )
    }

    fun <R> wrapCodecTag(key: String, codec: Codec<R>): Tag<R> {
        return wrapCodecTag(key, codec) {
            Transcoder.NBT
        }
    }

    fun componentListTag(key: String): Tag<List<Component>> {
        return wrapCodecTag(key, Codec.COMPONENT.list())
    }

    fun resolvableProfileTag(key: String): Tag<ResolvableProfile> {
        return wrapCodecTag(key, ResolvableProfile.CODEC)
    }

    fun bannerPatternsTag(key: String): Tag<BannerPatterns> {
        return wrapCodecTag(key, BannerPatterns.CODEC) {
            RegistryTranscoder(Transcoder.NBT, MinecraftServer.process())
        }
    }

    fun keyTag(key: String): Tag<Key> {
        return Tag.String(key).map(
            {
                string -> try {
                    return@map Key.key(string)
                }
                catch(e: InvalidKeyException) {
                    throw NBTReadError("", "invalid key", e)
                }
            }, Key::asString
        )
    }

    fun posTag(key: String): Tag<Pos> {
        return Tag.Double(key).list().map(
            {
                list ->
                when (list.size) {
                    3 -> Pos(list[0], list[1], list[2])
                    5 -> Pos(list[0], list[1], list[2], list[3].toFloat(), list[4].toFloat())
                    else -> throw NBTReadError("", "invalid list size: must be 3 or 5")
                }
            }, {
                pos -> listOf(pos.x, pos.y, pos.z, pos.yaw.toDouble(), pos.pitch.toDouble())
            }
        )
    }

    fun <T> TagReadable.getTagSourced(tag: Tag<T>): T? {
        try {
            return this.getTag(tag)
        }
        catch(e: NBTReadError) {
            throw e.addSource(tag.key())
        }
        catch(e: Exception) {
            throw NBTReadError(tag.key(), e)
        }
    }

    fun <T> TagReadable.getTagOrThrow(tag: Tag<T>): T {
        return this.getTagSourced(tag) ?: throw NBTReadError(tag.key(), "not found or null")
    }

    fun checkOrThrow(condition: Boolean, source: String, lazyMessage: () -> String) {
        if(!condition) throw NBTReadError(source, lazyMessage())
    }
}