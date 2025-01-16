package dev.vstz.datquarry.entity

import dev.vstz.datquarry.DatQuarryMod
import dev.vstz.datquarry.block.ModBlocks
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredRegister

// THIS LINE IS REQUIRED FOR USING PROPERTY DELEGATES
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModEntities {
    val REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DatQuarryMod.ID)

    val QUARRY_ENTITY = REGISTRY.register("quarry_entity") { ->
        BlockEntityType.Builder.of(
            ::QuarryEntity,
            ModBlocks.QuarryBlock.get()
        ).build(null)
    }
}
