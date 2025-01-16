package dev.vstz.datquarry.utils

import dev.vstz.datquarry.DatQuarryMod
import dev.vstz.datquarry.item.ModItems
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator
import net.minecraft.world.level.ItemLike
import net.neoforged.neoforge.registries.DeferredRegister


class ItemRenderer(vararg val items: ItemLike) : DisplayItemsGenerator {
    override fun accept(p0: CreativeModeTab.ItemDisplayParameters, p1: CreativeModeTab.Output) {
        items.forEach { item: ItemLike ->
            p1.accept(item)
        }
    }

}

object CreativeTaab {
    val REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DatQuarryMod.ID)

    val reg = REGISTRY.register("main") {
        ->
        CreativeModeTab.builder().title(
            Component.translatable("item_group.datquarry.dat-quarry")
        )
            .icon { ModItems.QUARRY_ITEM.toStack() }
            .displayItems(ItemRenderer(
                // Items
                ModItems.QUARRY_ITEM,
                ModItems.ENERGY_CORE,
                ModItems.QUARRY_BATTERY,
                ModItems.QUARRY_CU,
                ModItems.QUARRY_CORE,
                ModItems.SILK_CLOTH,
                ModItems.SILK,
                ModItems.SPEEDUP_2,
                ModItems.SPEEDUP_4,
                ModItems.SPEEDUP_8,
                ModItems.SPEEDUP_16,
                ModItems.SPEEDUP_32,
            ))
            .build()
    }
}