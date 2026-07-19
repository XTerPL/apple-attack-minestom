package org.joebobilly.appleattack.utils

import net.kyori.adventure.key.InvalidKeyException
import net.kyori.adventure.key.Key
import net.kyori.adventure.nbt.BinaryTag
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.codec.Codec
import net.minestom.server.codec.Result
import net.minestom.server.codec.Transcoder
import net.minestom.server.item.component.BannerPatterns
import net.minestom.server.network.player.ResolvableProfile
import net.minestom.server.registry.RegistryTranscoder
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagReadable
import java.util.Locale

object TagUtils {
    inline fun <reified R : Enum<R>> enumTag(key: String): Tag<R> {
        return Tag.String(key).map({
            string ->
            try {
                return@map enumValueOf<R>(string.uppercase(Locale.ROOT))
            }
            catch(_: IllegalArgumentException) {
                return@map null
            }
        }, {
            value -> value?.name?.lowercase(Locale.ROOT)
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
}