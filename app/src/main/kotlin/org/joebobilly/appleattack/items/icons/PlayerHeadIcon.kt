package org.joebobilly.appleattack.items.icons

import net.minestom.server.component.DataComponents
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.network.player.ResolvableProfile
import org.joebobilly.appleattack.utils.EnumUtils
import java.util.EnumSet

class PlayerHeadIcon(val profile: ResolvableProfile, flags: EnumSet<Flag> = EnumUtils.emptySet()) : ItemIcon(Material.PLAYER_HEAD, flags) {
    constructor(texturesBase64: String, flags: EnumSet<Flag> = EnumUtils.emptySet())
            : this(PlayerSkin(texturesBase64, null), flags)
    constructor(playerSkin: PlayerSkin, flags: EnumSet<Flag> = EnumUtils.emptySet())
            : this(ResolvableProfile(playerSkin), flags)

    override fun apply(builder: ItemStack.Builder) {
        super.apply(builder)
        builder.set(DataComponents.PROFILE, profile)
    }
}