package dev.vstz.datquarry.item

import dev.vstz.datquarry.DatQuarryMod
import dev.vstz.datquarry.block.ModBlocks
import net.neoforged.neoforge.registries.DeferredRegister

object ModItems {
    val REGISTRY = DeferredRegister.createItems(DatQuarryMod.ID)


    // Crafting Items
    val ENERGY_CORE = REGISTRY.registerSimpleItem("energy-core")
    val QUARRY_BATTERY = REGISTRY.registerSimpleItem("quarry-battery")
    val QUARRY_CU = REGISTRY.registerSimpleItem("quarry-control-unit")
    val QUARRY_CORE = REGISTRY.registerSimpleItem("quarry-core")
    val SILK_CLOTH = REGISTRY.registerSimpleItem("silk-cloth")

    val QUARRY_ITEM = REGISTRY.registerSimpleBlockItem(ModBlocks.QuarryBlock)

    val SILK = REGISTRY.registerSimpleBlockItem(ModBlocks.SILK)

    val SPEEDUP_2 = REGISTRY.registerSimpleBlockItem(ModBlocks.speedups[0])
    val SPEEDUP_4 = REGISTRY.registerSimpleBlockItem(ModBlocks.speedups[1])
    val SPEEDUP_8 = REGISTRY.registerSimpleBlockItem(ModBlocks.speedups[2])
    val SPEEDUP_16 = REGISTRY.registerSimpleBlockItem(ModBlocks.speedups[3])
    val SPEEDUP_32 = REGISTRY.registerSimpleBlockItem(ModBlocks.speedups[4])
}