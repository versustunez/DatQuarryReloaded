package dev.vstz.datquarry.utils

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3

object LootUtils {
    fun getLoot(level: ServerLevel, block: BlockState, pos: BlockPos, tool: ItemStack): List<ItemStack> {
        val lootParams = LootParams.Builder(level)
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
            .withParameter<BlockState>(LootContextParams.BLOCK_STATE, block)
            .withParameter<ItemStack>(LootContextParams.TOOL, tool)

        return block.getDrops(lootParams)
    }
}