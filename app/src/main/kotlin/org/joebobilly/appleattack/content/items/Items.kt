package org.joebobilly.appleattack.content.items

import org.joebobilly.appleattack.content.items.materials.attack.AppleSeed
import org.joebobilly.appleattack.content.items.materials.handles.AppleStick
import org.joebobilly.appleattack.content.items.tools.swords.StarterSword
import org.joebobilly.appleattack.content.items.upgrades.CreativeModifier
import org.joebobilly.appleattack.items.AAItemManager
import org.joebobilly.appleattack.items.tools.type.SwordItem

object Items {
    fun register() {
        AAItemManager.register(AppleItem)
        AAItemManager.register(GreenAppleItem)
        AAItemManager.register(CreativeModifier)
        AAItemManager.register(SwordItem.Forged)
        AAItemManager.register(StarterSword)
        AAItemManager.register(AppleStick)
        AAItemManager.register(AppleSeed)
    }
}