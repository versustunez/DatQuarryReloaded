package dev.vstz.datquarry.block

import dev.vstz.datquarry.DatQuarryMod
import dev.vstz.datquarry.entity.QuarryEntity
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister

// THIS LINE IS REQUIRED FOR USING PROPERTY DELEGATES
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModBlocks {
    val REGISTRY = DeferredRegister.createBlocks(DatQuarryMod.ID)

    val QuarryBlock = REGISTRY.register("quarry") { ->
        Quarry(
            BlockBehaviour.Properties.of()
                .strength(2.0f)
                .destroyTime(1.0f)
                .explosionResistance(1000.0f)
                .sound(SoundType.NETHERITE_BLOCK)
        )
    }

    val speedups = ArrayList<DeferredBlock<Speedup>>()


    init {
        arrayOf(2, 4, 8, 16, 32).forEach {
            speedups.add(REGISTRY.register("speedup_$it") {
                ->
                Speedup(it)
            })
        }
    }

    val SILK = REGISTRY.register("silk") {
        ->
        Silk()
    }
}
